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

@Service
@Primary
public class BookServiceImpl implements BookService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<String, Long> getBooks() {

        List<Book> books = entityManager.createQuery("SELECT b FROM Book b", Book.class).getResultList();

        Map<String, Long> genreCount = books.stream()
                .flatMap(book -> book.getGenres().stream())
                .collect(Collectors.groupingBy(genre -> genre, Collectors.counting()));

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

        StringBuilder queryBuilder = new StringBuilder("SELECT b FROM Book b WHERE 1=1");
        Map<String, Object> parameters = new HashMap<>();

        if (searchCriteria.getTitle() != null && !searchCriteria.getTitle().isBlank()) {
            queryBuilder.append(" AND LOWER(b.title) LIKE LOWER(:title)");
            parameters.put("title", "%" + searchCriteria.getTitle() + "%");
        }

        if (searchCriteria.getAuthor() != null && !searchCriteria.getAuthor().isBlank()) {
            queryBuilder.append(" AND LOWER(b.author) LIKE LOWER(:author)");
            parameters.put("author", "%" + searchCriteria.getAuthor() + "%");
        }

        if (searchCriteria.getDescription() != null && !searchCriteria.getDescription().isBlank()) {
            queryBuilder.append(" AND LOWER(b.description) LIKE LOWER(:description)");
            parameters.put("description", "%" + searchCriteria.getDescription() + "%");
        }

        if (searchCriteria.getYear() != null) {
            queryBuilder.append(" AND EXTRACT(YEAR FROM b.publicationDate) = :year");
            parameters.put("year", searchCriteria.getYear());
        }

        queryBuilder.append(" ORDER BY b.publicationDate DESC");

        TypedQuery<Book> query = entityManager.createQuery(queryBuilder.toString(), Book.class);
        parameters.forEach(query::setParameter);

        List<Book> books = query.getResultList();

        if (searchCriteria.getGenre() != null && !searchCriteria.getGenre().isEmpty()) {
            books = books.stream()
                    .filter(book -> book.getGenres() != null && book.getGenres().contains(searchCriteria.getGenre()))
                    .collect(Collectors.toList());
        }

        return books;
    }
}
