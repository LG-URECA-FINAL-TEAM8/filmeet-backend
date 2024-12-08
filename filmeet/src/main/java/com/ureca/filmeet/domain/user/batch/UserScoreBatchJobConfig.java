package com.ureca.filmeet.domain.user.batch;

import com.ureca.filmeet.domain.user.entity.User;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UserScoreBatchJobConfig {

    @Bean
    public Job userScoreUpdateJob(JobRepository jobRepository, Step calculateStatsStep, Step userScoreUpdateStep) {
        return new JobBuilder("userScoreUpdateJob", jobRepository)
                .start(calculateStatsStep) // 통계 계산 Step 먼저 실행
                .next(userScoreUpdateStep) // 사용자 점수 업데이트 Step 실행
                .build();
    }

    @Bean
    public Step calculateStatsStep(JobRepository jobRepository, Tasklet calculateStatsTasklet,
                                   PlatformTransactionManager transactionManager) {
        return new StepBuilder("calculateStatsStep", jobRepository)
                .tasklet(calculateStatsTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step userScoreUpdateStep(JobRepository jobRepository, ItemReader<User> userItemReader,
                                    ItemProcessor<User, User> userProcessor, ItemWriter<User> userItemWriter,
                                    PlatformTransactionManager transactionManager) {
        return new StepBuilder("userScoreUpdateStep", jobRepository)
                .<User, User>chunk(100, transactionManager)
                .reader(userItemReader)
                .processor(userProcessor)
                .writer(userItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<User> userItemReader(DataSource dataSource) {
        JdbcPagingItemReader<User> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setFetchSize(100);

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("SELECT member_id, total_movie_likes, total_collections, total_games");
        queryProvider.setFromClause("FROM member");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("member_id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);

        reader.setQueryProvider(queryProvider);
        reader.setRowMapper((rs, rowNum) -> {
            Long memberId = rs.getLong("member_id");
            return new User(memberId, rs.getInt("total_movie_likes"), rs.getInt("total_collections"),
                    rs.getInt("total_games"));
        });
        return reader;
    }

    @Bean
    @StepScope
    public ItemProcessor<User, User> userProcessor() {
        return user -> {
            ExecutionContext executionContext = StepSynchronizationManager.getContext()
                    .getStepExecution()
                    .getJobExecution()
                    .getExecutionContext();

            int maxLikes = getExecutionContextValue(executionContext, "maxLikes", Long.class, 0L).intValue();
            int minLikes = getExecutionContextValue(executionContext, "minLikes", Long.class, 0L).intValue();
            double averageLikes = getExecutionContextValue(executionContext, "averageLikes", BigDecimal.class,
                    BigDecimal.ZERO).doubleValue();

            int maxGames = getExecutionContextValue(executionContext, "maxGames", Long.class, 0L).intValue();
            int minGames = getExecutionContextValue(executionContext, "minGames", Long.class, 0L).intValue();
            double averageGames = getExecutionContextValue(executionContext, "averageGames", BigDecimal.class,
                    BigDecimal.ZERO).doubleValue();

            int maxCollections = getExecutionContextValue(executionContext, "maxCollections", Long.class,
                    0L).intValue();
            int minCollections = getExecutionContextValue(executionContext, "minCollections", Long.class,
                    0L).intValue();
            double averageCollections = getExecutionContextValue(executionContext, "averageCollections",
                    BigDecimal.class, BigDecimal.ZERO).doubleValue();

            user.adjustActivityScores(
                    maxLikes, minLikes, maxGames, minGames, maxCollections, minCollections,
                    averageLikes, averageGames, averageCollections
            );
            return user;
        };
    }

    private <T> T getExecutionContextValue(ExecutionContext executionContext, String key, Class<T> type,
                                           T defaultValue) {
        Object value = executionContext.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return defaultValue;
    }

    @Bean
    @StepScope
    public ItemWriter<User> userItemWriter(DataSource dataSource) {
        return items -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            String updateSql = "UPDATE member SET like_activity_score = ?, collection_activity_score = ?, game_activity_score = ? WHERE member_id = ?";
            jdbcTemplate.batchUpdate(updateSql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    List<? extends User> userList = items.getItems();
                    User user = userList.get(i);
                    ps.setInt(1, user.getLikeActivityScore());
                    ps.setInt(2, user.getCollectionActivityScore());
                    ps.setInt(3, user.getGameActivityScore());
                    ps.setLong(4, user.getId());
                }

                @Override
                public int getBatchSize() {
                    return items.getItems().size();
                }
            });

        };
    }

    @Bean
    public Tasklet calculateStatsTasklet(DataSource dataSource) {
        return (contribution, chunkContext) -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

            Map<String, Object> likeStats = jdbcTemplate.queryForMap(
                    "SELECT MAX(like_count) AS maxLikes, MIN(like_count) AS minLikes, AVG(like_count) AS averageLikes "
                            +
                            "FROM ( " +
                            "    SELECT COUNT(member_id) AS like_count " +
                            "    FROM movie_likes " +
                            "    GROUP BY member_id " +
                            ") AS like_counts"
            );

            Map<String, Object> gameStats = jdbcTemplate.queryForMap(
                    "SELECT MAX(game_count) AS maxGames, MIN(game_count) AS minGames, AVG(game_count) AS averageGames "
                            + "FROM ( "
                            + "    SELECT COUNT(*) AS game_count "
                            + "    FROM game_result "
                            + "    GROUP BY member_id "
                            + ") AS game_scores;"
            );

            Map<String, Object> collectionStats = jdbcTemplate.queryForMap(
                    "SELECT MAX(collection_count) AS maxCollections, MIN(collection_count) AS minCollections, AVG(collection_count) AS averageCollections "
                            +
                            "FROM ( " +
                            "    SELECT COUNT(member_id) AS collection_count " +
                            "    FROM collection_likes " +
                            "    GROUP BY member_id " +
                            ") AS collection_counts"
            );

            ExecutionContext jobExecutionContext = chunkContext.getStepContext()
                    .getStepExecution()
                    .getJobExecution()
                    .getExecutionContext();

            jobExecutionContext.put("maxLikes", likeStats.get("maxLikes"));
            jobExecutionContext.put("minLikes", likeStats.get("minLikes"));
            jobExecutionContext.put("averageLikes", likeStats.get("averageLikes"));

            jobExecutionContext.put("maxGames", gameStats.get("maxGames"));
            jobExecutionContext.put("minGames", gameStats.get("minGames"));
            jobExecutionContext.put("averageGames", gameStats.get("averageGames"));

            jobExecutionContext.put("maxCollections", collectionStats.get("maxCollections"));
            jobExecutionContext.put("minCollections", collectionStats.get("minCollections"));
            jobExecutionContext.put("averageCollections", collectionStats.get("averageCollections"));

            log.info("통계 데이터가 ExecutionContext에 저장되었습니다.");
            return RepeatStatus.FINISHED;
        };
    }
}