package demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.domain.Bookmark;

import java.util.Collection;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Collection<Bookmark> findByAccountUsername(String username);
}
