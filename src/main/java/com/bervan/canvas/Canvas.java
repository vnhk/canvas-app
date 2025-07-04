package com.bervan.canvas;

import com.bervan.common.model.BervanBaseEntity;
import com.bervan.common.model.VaadinBervanColumn;
import com.bervan.history.model.HistoryCollection;
import com.bervan.history.model.HistorySupported;
import com.bervan.ieentities.ExcelIEEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@HistorySupported
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "owner.id"})
)
public class Canvas extends BervanBaseEntity<UUID> implements ExcelIEEntity<UUID> {
    @Id
    private UUID id;
    @Size(max = 100)
    @VaadinBervanColumn(internalName = "name", displayName = "Name")
    private String name;
    @Lob
    @Size(max = 5000000)
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    private LocalDateTime modificationDate;
    private LocalDateTime creationDate;
    @VaadinBervanColumn(internalName = "category", displayName = "Category")
    private String category;

    private Boolean deleted = false;

    @OneToMany(fetch = FetchType.EAGER)
    @HistoryCollection(historyClass = HistoryCanvas.class)
    private Set<HistoryCanvas> history = new HashSet<>();

    public Canvas() {

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    @Override
    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Set<HistoryCanvas> getHistory() {
        return history;
    }

    public void setHistory(Set<HistoryCanvas> history) {
        this.history = history;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean isDeleted() {
        if (deleted == null) {
            return false;
        }
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}