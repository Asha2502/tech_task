package com.ifortex.bookservice.service.impl;

import com.ifortex.bookservice.model.Book;
import com.ifortex.bookservice.model.Member;
import com.ifortex.bookservice.service.MemberService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service implementation for managing members.
 * This class provides methods to find specific members based on certain criteria.
 * It uses JPA's EntityManager to interact with the database.
 */

@Service
@Primary
public class MemberServiceImpl implements MemberService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Member findMember() {
        Member resultMember = null;
        LocalDateTime oldestBookDate = LocalDateTime.MAX;

        // Fetch all members from the database
        List<Member> members = entityManager.createQuery("SELECT m FROM Member m", Member.class).getResultList();

        // Iterate through each member to find the one meeting the criteria
        for (Member member : members) {
            List<Book> romanceBooks = member.getBorrowedBooks().stream()
                    .filter(book -> book.getGenres().contains("Romance"))
                    .toList();

            // If the member has borrowed Romance books, check for the oldest book
            if (!romanceBooks.isEmpty()) {
                Book oldestRomanceBook = romanceBooks.stream()
                        .min(Comparator.comparing(Book::getPublicationDate))
                        .orElseThrow(() -> new RuntimeException("No Romance books found"));

                // Update the result if this member's oldest Romance book is older
                if (oldestRomanceBook.getPublicationDate().isBefore(oldestBookDate)) {
                    resultMember = member;
                    oldestBookDate = oldestRomanceBook.getPublicationDate();
                } else if (oldestRomanceBook.getPublicationDate().isEqual(oldestBookDate)) {
                    if (member.getMembershipDate().isBefore(resultMember.getMembershipDate())) {
                        resultMember = member;
                    }
                }
            }
        }

        return resultMember;
    }

    @Override
    public List<Member> findMembers() {
        List<Member> membersRegisteredIn2023 = new ArrayList<>();

        // Fetch all members from the database
        List<Member> members = entityManager.createQuery("SELECT m FROM Member m", Member.class).getResultList();

        // Filter members who registered in 2023 and have no borrowed books
        for (Member member : members) {
            if (member.getMembershipDate().getYear() == 2023 && member.getBorrowedBooks().isEmpty()) {
                membersRegisteredIn2023.add(member);
            }
        }

        return membersRegisteredIn2023;
    }
}
