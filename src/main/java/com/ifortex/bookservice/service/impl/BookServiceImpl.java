package com.ifortex.bookservice.service.impl;

import com.ifortex.bookservice.dto.SearchCriteria;
import com.ifortex.bookservice.model.Book;
import com.ifortex.bookservice.service.BookService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation for managing books.
 * This class provides methods to retrieve books and filter them based on search criteria.
 * It uses JPA's EntityManager to interact with the database.
 */

@Service
@Primary
public class BookServiceImpl implements BookService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Long> getBooks() {

        // Fetch all books from the database
        List<Book> books = entityManager.createQuery("SELECT b FROM Book b", Book.class).getResultList();

        // Count books by genre
        Map<String, Long> genreCount = books.stream()
                .flatMap(book -> book.getGenres().stream())
                .collect(Collectors.groupingBy(genre -> genre, Collectors.counting()));

        // Sort genres by count in order from the genre with the most books to the least
        return genreCount.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    @Override
    public List<Book> getAllByCriteria(SearchCriteria searchCriteria) {

        // Build the query dynamically based on the search criteria
        StringBuilder queryBuilder = new StringBuilder("SELECT b FROM Book b WHERE 1=1");
        Map<String, Object> parameters = new HashMap<>();

        // Filter by title if provided
        if (searchCriteria.getTitle() != null && !searchCriteria.getTitle().isBlank()) {
            queryBuilder.append(" AND LOWER(b.title) LIKE LOWER(:title)");
            parameters.put("title", "%" + searchCriteria.getTitle() + "%");
        }

        // Filter by author if provided
        if (searchCriteria.getAuthor() != null && !searchCriteria.getAuthor().isBlank()) {
            queryBuilder.append(" AND LOWER(b.author) LIKE LOWER(:author)");
            parameters.put("author", "%" + searchCriteria.getAuthor() + "%");
        }

        // Filter by description if provided
        if (searchCriteria.getDescription() != null && !searchCriteria.getDescription().isBlank()) {
            queryBuilder.append(" AND LOWER(b.description) LIKE LOWER(:description)");
            parameters.put("description", "%" + searchCriteria.getDescription() + "%");
        }

        // Filter by publication year if provided
        if (searchCriteria.getYear() != null) {
            queryBuilder.append(" AND EXTRACT(YEAR FROM b.publicationDate) = :year");
            parameters.put("year", searchCriteria.getYear());
        }

        // Add sorting by publication date
        queryBuilder.append(" ORDER BY b.publicationDate DESC");

        // Create and execute the query
        TypedQuery<Book> query = entityManager.createQuery(queryBuilder.toString(), Book.class);
        parameters.forEach(query::setParameter);

        List<Book> books = query.getResultList();

        // Further filter by genre if provided
        if (searchCriteria.getGenre() != null && !searchCriteria.getGenre().isEmpty()) {
            books = books.stream()
                    .filter(book -> book.getGenres() != null && book.getGenres().contains(searchCriteria.getGenre()))
                    .collect(Collectors.toList());
        }

        return books;
    }
}
