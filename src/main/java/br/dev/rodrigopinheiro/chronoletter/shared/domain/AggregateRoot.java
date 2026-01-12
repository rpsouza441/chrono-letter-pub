package br.dev.rodrigopinheiro.chronoletter.shared.domain;

import java.time.Instant;
import java.util.Objects;

public abstract class AggregateRoot<ID> {

    protected final ID id;
    protected final Instant createdAt;
    protected Instant updatedAt;

  protected AggregateRoot(ID id) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }
    
    // Para reconstrução do banco
    protected AggregateRoot(ID id, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public ID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    protected void touch() {
        this.updatedAt = Instant.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateRoot<?> that = (AggregateRoot<?>) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
