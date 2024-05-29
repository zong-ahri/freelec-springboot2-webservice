package com.jojoldu.book.springboot.domain.posts;

import org.junit.jupiter.api.AfterEach; // import org.junit.After;
import org.junit.jupiter.api.Test; // import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith; // import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension; // import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class) // @RunWith(SpringRunner.class) : Junit4 미만, 테스트를 진행할 때 JUnit에 내장된 실행자 외에 다른 실행자를 실행 // @ExtendWith(SpringExtension.class) : Junit5 이상
@SpringBootTest
public class PostsRepositoryTest {

    @Autowired
    PostsRepository postsRepository;

    @AfterEach // Junit에서 단위 테스트가 끝날 때마다 수행되는 메소드를 지정
    public void cleanup() {
        postsRepository.deleteAll();
    }

    @Test
    public void 게시글저장_불러오기() {
        // given
        String title = "테스트 게시글";
        String content = "테스트 본문";

        postsRepository.save(Posts.builder() // 테이블 posts에 id 값이 있다면 update 없다면 insert 쿼리 실행
                .title(title)
                .content(content)
                .author("jojoldu@gmail.com")
                .build());

        // when
        List<Posts> postsList = postsRepository.findAll(); // 테이블 posts에 있는 모든 데이터를 조회해오는 메소드

        // then
        Posts posts = postsList.get(0);
        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(content);
    }
}
