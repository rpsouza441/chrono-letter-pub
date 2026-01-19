package br.dev.rodrigopinheiro.chronoletter.letter.infrastructure.adapter.out.persistence;

import br.dev.rodrigopinheiro.chronoletter.letter.domain.Letter;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.LetterContent;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.LetterId;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.ScheduleInfo;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.enums.LetterStatus;
import br.dev.rodrigopinheiro.chronoletter.user.domain.Timezone;
import br.dev.rodrigopinheiro.chronoletter.user.domain.UserId;

public class LetterMapper {
    public static LetterEntity toEntity(Letter letter) {
        LetterEntity entity = new LetterEntity();
        entity.setId(letter.getId().value());
        entity.setUserId(letter.getOwnerId().value());
        entity.setContent(letter.getContent().value());
        entity.setScheduledDate(letter.getScheduleInfo().scheduledDate());
        entity.setTimezone(letter.getScheduleInfo().timezone().value());
        entity.setSendAt(letter.getScheduleInfo().sendAt());
        entity.setSendHourLocalUsed(letter.getScheduleInfo().sendHourLocalUsed());
        entity.setStatus(toEntityStatus(letter.getStatus()));
        entity.setSentAt(letter.getSentAt());
        entity.setFailureReason(letter.getFailureReason());
        entity.setCreatedAt(letter.getCreatedAt());
        entity.setUpdatedAt(letter.getUpdatedAt());
        return entity;
    }

    public static Letter toDomain(LetterEntity entity) {
        return Letter.reconstitute(
                LetterId.of(entity.getId()),
                UserId.of(entity.getUserId()),
                LetterContent.of(entity.getContent()),
                reconstructScheduleInfo(entity),
                toDomainStatus(entity.getStatus()),
                entity.getSentAt(),
                entity.getFailureReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private static ScheduleInfo reconstructScheduleInfo(LetterEntity entity) {
        if (entity.getSendAt() != null) {
            // Já materializado - precisa reconstruir com sendAt
            return ScheduleInfo.reconstitute(
                    entity.getScheduledDate(),
                    Timezone.of(entity.getTimezone()),
                    entity.getSendAt(),
                    entity.getSendHourLocalUsed());
        }
        return ScheduleInfo.of(entity.getScheduledDate(), Timezone.of(entity.getTimezone()));
    }

    private static LetterStatusEntity toEntityStatus(LetterStatus status) {
        return LetterStatusEntity.valueOf(status.name());
    }

    private static LetterStatus toDomainStatus(LetterStatusEntity status) {
        return LetterStatus.valueOf(status.name());
    }

}
