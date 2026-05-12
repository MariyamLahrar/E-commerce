package com.commerce.Online.repository;

import com.commerce.Online.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByActiveTrue(Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.active = true AND " +
            "(:search IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%',:search,'%')) " +
            "OR LOWER(a.description) LIKE LOWER(CONCAT('%',:search,'%')) " +
            "OR LOWER(a.brand) LIKE LOWER(CONCAT('%',:search,'%'))) " +
            "AND (:category IS NULL OR a.category = :category)")
    Page<Article> searchArticles(@Param("search") String search,
                                 @Param("category") String category,
                                 Pageable pageable);

    @Query("SELECT DISTINCT a.category FROM Article a WHERE a.active = true ORDER BY a.category")
    List<String> findDistinctCategories();

    @Query("SELECT a FROM Article a WHERE a.active = true AND a.stock > 0")
    Page<Article> findAvailableArticles(Pageable pageable);
}
