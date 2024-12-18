Tech Task Requirements

Installed tools requirements:
Java 17,
Maven,
Any IDE,
Docker.

In the project, you will find a docker-compose package. Run docker compose up to start the services. During the first application startup, database migration will execute and populate the necessary data.

Overview

You are working on a book service platform and are required to implement two services: BookService and MemberService. Please note that certain classes have restrictions and cannot be edited.

Tasks:
* BookService Implementation.

Implement the BookService interface and place your implementation in the existing impl package (preserving the existing classes inside the service).
      Required Methods:
  1. Count books by genre.

    Implement a method that retrieves the total count of books for each genre, ordered from the genre with the most books to the least.
  2. Search books with filters. Implement a method that returns books matching the provided search criteria, sorted by publication date.

    The search should support partial matches for fields such as title, author, etc. If no parameters are passed or all parameters are blank, return all books.

    For example, searching by title for the word “Wonderland”(Alice’s Adventures in Wonderland) should return any book(s) that contains ‘Wonderland’ in its title.
    All text fields should support this type of partial matching, and all parameters can be used together. Note that the genre field should be passed in singular form.
* MemberService Implementation.

Implement the MemberService interface and place your implementation in the existing impl package (preserving the existing classes inside the service).
      Required Methods:
  1. Find the member with the oldest Romance book.

    Implement a method that finds and returns the member who has read the oldest “Romance” genre book and who was most recently registered on the platform.
  2. Find members registered in 2023 who haven’t read any books.

    Implement a method that finds and returns members who registered in 2023 but have not read any books.

### Results

* BookService Implementation

1. URL http://localhost:8080/api/v1/books/statistic

**Response** 
{
"Fiction": 13,
"Non-Fiction": 7,
"Classic": 4,
"Adventure": 3,
"Dystopian": 3,
"Biography": 3,
"Romance": 2,
"Self-Help": 2,
"Fantasy": 2,
"Memoir": 1,
"Horror": 1,
"History": 1,
"Thriller": 1,
"Mystery": 1,
"Philosophical": 1,
"Historical": 1
}

2. URL http://localhost:8080/api/v1/books/search

**RequestBody**
{
"author": "J.",
"year": "1997"
}

**Response**
[
{
"id": 13,
"title": "Harry Potter and the Sorcerer's Stone",
"description": "A young boy discovers he is a wizard.",
"author": "J.K. Rowling",
"publicationDate": "1997-06-26T00:00:00",
"genres": [
"Adventure",
"Fantasy"
]
}
]

**RequestBody**
{
"genre": "Self-Help"
}

**Response**
[
{
"id": 22,
"title": "The Power of Habit",
"description": "A book about the science of habit formation.",
"author": "Charles Duhigg",
"publicationDate": "2012-02-28T00:00:00",
"genres": [
"Self-Help",
"Non-Fiction"
]
},

{
"id": 17,
"title": "The Road Less Traveled",
"description": "A book about personal growth and spiritual development.",
"author": "M. Scott Peck",
"publicationDate": "1978-01-01T00:00:00",
"genres": [
"Self-Help",
"Non-Fiction"
]
}
]

* MemberService Implementation

1. URL http://localhost:8080/api/v1/members/amateur

**Response**
{
"id": 11,
"name": "Kevin Hart",
"membershipDate": "2023-04-02T10:45:00",
"borrowedBooks": [
{
"id": 1,
"title": "The Great Gatsby",
"description": "A novel about the American dream.",
"author": "F. Scott Fitzgerald",
"publicationDate": "1925-04-10T00:00:00",
"genres": [
"Classic",
"Fiction"
]
},
{
"id": 7,
"title": "Pride and Prejudice",
"description": "A romantic novel about manners.",
"author": "Jane Austen",
"publicationDate": "1813-01-28T00:00:00",
"genres": [
"Romance",
"Fiction"
]
}
]
}

2. URL http://localhost:8080/api/v1/members

**Response**
[
{
"id": 19,
"name": "Steve Rogers",
"membershipDate": "2023-10-11T13:20:00",
"borrowedBooks": []
}
]
