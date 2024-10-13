package com.example.demo.controller;

import com.example.demo.Model.BookModel;
import com.example.demo.Service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }


    @GetMapping
    public List<BookModel> getAllBook()
    {
            return bookService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookModel> getByIdBook(@PathVariable Long id)
    {
        Optional<BookModel> book = bookService.getById(id);
        return book.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Thêm sách mới (POST)
    @PostMapping
    public ResponseEntity<BookModel> addBook(@RequestBody BookModel newBook) {
        BookModel book = bookService.add(newBook);
        return new ResponseEntity<>(book, HttpStatus.CREATED );
    }

    // Cập nhật sách (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<BookModel> updateBook(@PathVariable Long id, @RequestBody BookModel updatedBook) {
        Optional<BookModel> book = bookService.update(id, updatedBook);
        return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Xóa sách (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        boolean isDeleted = bookService.delete(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
