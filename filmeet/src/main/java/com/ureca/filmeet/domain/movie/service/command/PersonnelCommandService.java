package com.ureca.filmeet.domain.movie.service.command;

import com.ureca.filmeet.domain.movie.entity.Personnel;
import com.ureca.filmeet.domain.movie.repository.PersonnelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonnelCommandService {
    private final PersonnelRepository personnelRepository;

    public Personnel findOrCreatePersonnel(Integer staffId, String name) {
        return personnelRepository.findByStaffId(staffId)
                .orElseGet(() -> {
                    return personnelRepository.save(Personnel.builder()
                            .staffId(staffId)
                            .name(name)
                            .build());
                });
    }
}
