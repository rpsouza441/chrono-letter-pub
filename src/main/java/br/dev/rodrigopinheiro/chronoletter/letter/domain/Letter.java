package br.dev.rodrigopinheiro.chronoletter.letter.domain;

import java.time.Instant;
import java.util.Objects;

import br.dev.rodrigopinheiro.chronoletter.letter.domain.enums.LetterStatus;
import br.dev.rodrigopinheiro.chronoletter.letter.domain.exception.LetterAlreadySentException;
import br.dev.rodrigopinheiro.chronoletter.shared.domain.AggregateRoot;
import br.dev.rodrigopinheiro.chronoletter.user.domain.UserId;

public class Letter extends AggregateRoot<LetterId> {

    private final UserId ownerId;
    private LetterContent content;
    private ScheduleInfo scheduleInfo;
    private LetterStatus status;
    private Instant sentAt;
    private String failureReason;

    // Construtor para nova carta (rascunho)
    private Letter(LetterId id, UserId ownerId, LetterContent content, ScheduleInfo scheduleInfo) {
        super(id);
        this.ownerId = Objects.requireNonNull(ownerId, "Owner ID cannot be null");
        this.content = Objects.requireNonNull(content, "Content cannot be null");
        this.scheduleInfo = Objects.requireNonNull(scheduleInfo, "Schedule info cannot be null");
        this.status = LetterStatus.DRAFT;
    }

    // Construtor para reconstituição
    private Letter(
            LetterId id,
            UserId ownerId,
            LetterContent content,
            ScheduleInfo scheduleInfo,
            LetterStatus status,
            Instant sentAt,
            String failureReason,
            Instant createdAt,
            Instant updatedAt) {
        super(id, createdAt, updatedAt);
        this.ownerId = ownerId;
        this.content = content;
        this.scheduleInfo = scheduleInfo;
        this.status = status;
        this.sentAt = sentAt;
        this.failureReason = failureReason;
    }

    // Factory para novo rascunho
    public static Letter draft(UserId ownerId, LetterContent content, ScheduleInfo scheduleInfo) {
        return new Letter(LetterId.generate(), ownerId, content, scheduleInfo);
    }

    // Factory para reconstituição do banco
    public static Letter reconstitute(
            LetterId id,
            UserId ownerId,
            LetterContent content,
            ScheduleInfo scheduleInfo,
            LetterStatus status,
            Instant sentAt,
            String failureReason,
            Instant createdAt,
            Instant updatedAt) {
        return new Letter(id, ownerId, content, scheduleInfo, status, sentAt, failureReason, createdAt, updatedAt);
    }

    // ========== Comportamentos de Domínio ==========

    public void schedule() {
        ensureCanModify();
        this.status = LetterStatus.PENDING;
        touch();
    }

    public void updateContent(LetterContent newContent) {
        ensureCanModify();
        this.content = Objects.requireNonNull(newContent);
        touch();
    }

    public void updateSchedule(ScheduleInfo newSchedule) {
        ensureCanModify();
        if (this.scheduleInfo.isMaterialized()) {
            throw new IllegalStateException("Cannot update schedule after materialization");
        }
        this.scheduleInfo = Objects.requireNonNull(newSchedule);
        touch();
    }

    public void materializeSendAt(int sendHourLocal) {
        if (status != LetterStatus.PENDING) {
            throw new IllegalStateException("Can only materialize PENDING letters");
        }
        this.scheduleInfo = this.scheduleInfo.materialize(sendHourLocal);
        touch();
    }

    public void markAsSending() {
        if (status != LetterStatus.PENDING) {
            throw new IllegalStateException("Can only send PENDING letters");
        }
        this.status = LetterStatus.SENDING;
        touch();
    }

    public void markAsSent() {
        if (status != LetterStatus.SENDING) {
            throw new IllegalStateException("Can only mark SENDING letters as sent");
        }
        this.status = LetterStatus.SENT;
        this.sentAt = Instant.now();
        touch();
    }

    public void markAsFailed(String reason) {
        if (status != LetterStatus.SENDING) {
            throw new IllegalStateException("Can only mark SENDING letters as failed");
        }
        this.status = LetterStatus.FAILED;
        this.failureReason = reason;
        touch();
    }

    public void retryDelivery() {
        if (status != LetterStatus.FAILED && status != LetterStatus.SENDING) {
            throw new IllegalStateException("Can only retry FAILED or SENDING letters");
        }
        this.status = LetterStatus.PENDING;
        touch();
    }

    private void ensureCanModify() {
        if (status.isFinal()) {
            throw new LetterAlreadySentException();
        }
    }

    // ========== Getters ==========

    public UserId getOwnerId() {
        return ownerId;
    }

    public LetterContent getContent() {
        return content;
    }

    public ScheduleInfo getScheduleInfo() {
        return scheduleInfo;
    }

    public LetterStatus getStatus() {
        return status;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

}
