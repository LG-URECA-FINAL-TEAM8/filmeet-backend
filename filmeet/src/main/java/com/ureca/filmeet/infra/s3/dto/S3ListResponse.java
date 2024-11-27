package com.ureca.filmeet.infra.s3.dto;

import java.util.List;

public record S3ListResponse(List<String> files
                             ) {
}
