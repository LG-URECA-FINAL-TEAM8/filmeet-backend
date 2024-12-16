package com.ureca.filmeet.domain.movie.batch;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MovieBatchChunkListener implements ChunkListener {

    @Override
    public void beforeChunk(ChunkContext context) {
        String stepName = context.getStepContext().getStepExecution().getStepName();
        log.info("스텝 '{}'에서 Chunk 처리를 시작합니다. 시작 시간: {}", stepName, LocalDateTime.now());
    }

    @Override
    public void afterChunk(ChunkContext context) {
        String stepName = context.getStepContext().getStepExecution().getStepName();
        long itemCount = context.getStepContext().getStepExecution().getWriteCount();
        log.info("스텝 '{}'에서 Chunk 처리가 완료되었습니다. 완료 시간: {} | 처리된 Item 수: {}",
                stepName, LocalDateTime.now(), itemCount);
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        String stepName = context.getStepContext().getStepExecution().getStepName();
        Throwable exception = context.getStepContext().getStepExecution().getFailureExceptions()
                .stream().findFirst().orElse(null);
        log.error("스텝 '{}'에서 Chunk 처리 중 오류가 발생했습니다. 발생 시간: {} | 오류 내용: {}",
                stepName, LocalDateTime.now(), exception != null ? exception.getMessage() : "알 수 없는 오류");
    }
}