package com.ediae.ecotrack_office.shared.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public class PageResponseDto<T> {

    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalElements;

    // Spring necesita un constructor vacío para poder crear el objeto
    public PageResponseDto() {}

    /**
     * Constructor de conveniencia: recibe un Page de Spring y lo convierte
     * al formato que queremos devolver al frontend.
     *
     * @param page   el resultado paginado que devuelve el Repository
     * @param content la lista ya convertida a DTOs (no entidades)
     */
    public PageResponseDto(Page<?> page, List<T> content) {
        this.content = content;
        this.currentPage = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
    }

    // Getters y Setters
    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
}
