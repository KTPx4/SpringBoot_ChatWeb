package com.example.demo.Service;

import com.example.demo.Model.BookModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private List<BookModel> books = new ArrayList<>();

    // Constructor để thêm vài cuốn sách mẫu
    public BookService() {
        books.add(new BookModel(1L, "Spring Boot in Action", "Craig Walls", 29.99));
        books.add(new BookModel(2L, "Clean Code", "Robert C. Martin", 25.00));
        books.add(new BookModel(3L, "Effective Java", "Joshua Bloch", 32.00));
    }

    public List<BookModel> getAll()
    {
        return books;
    }

    public Optional<BookModel> getById(Long id)
    {
        return books.stream().filter(book -> book.getId().equals(id)).findFirst();
    }

    // Thêm sách mới
    public BookModel add(BookModel book) {
        books.add(book);
        return book;
    }

    public Optional<BookModel> update(long id, BookModel booku)
    {
        Optional<BookModel> existBook = getById(id);
        existBook.ifPresent(b -> {
            b.setAuthor(booku.getAuthor());
            b.setPrice(booku.getPrice());
            b.setTitle(booku.getTitle());
        });
        return existBook;
    }
    public boolean delete(Long id)
    {
        return books.removeIf(b -> b.getId().equals(id));
    }
}
