package com.jojoldu.book.springboot.web;

import org.junit.jupiter.api.Test; // import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith; // import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension; // import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class) // @RunWith(SpringRunner.class) : Junit4 미만, 테스트를 진행할 때 JUnit에 내장된 실행자 외에 다른 실행자를 실행 // @ExtendWith : Junit5 이상
@WebMvcTest(controllers = HelloController.class) // @WebMvcTest : 여러 스프링 테스트 어노테이션 중, Web(Spring MVC)에 집중할 수 있는 어노테이션
public class HelloControllerTest {

    @Autowired // @Autowired : 스프링이 관리하는 빈(Bean)을 주입 받음
    private MockMvc mvc; // private MockMvc mvc : 웹 API를 테스트할 때 사용, 스프링 MVC 테스트의 시작점

    @Test
    public void hello가_리턴된다() throws Exception {
        String hello = "hello";

        mvc.perform(get("/hello")) // mvc.perform(get("/hello")) : MockMvc를 통해 /hello 주소로 HTTP GET 요청
                .andExpect(status().isOk()) // andExpect(status().isOk()) : HTTP Header의 Status를 검증 (200, 404, 500)
                .andExpect(content().string(hello)); // andExpect(content().string(hello)) : 응답 본문의 내용을 검증 ("hello" 리턴)
    }

    @Test
    public void helloDto가_리턴된다() throws Exception {
        String name = "hello";
        int amount = 1000;

        mvc.perform(get("/hello/dto")
                        .param("name", name) // param : API 테스트할 때 사용될 요청 파라미터를 설정
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(name))) // jsonPath : JSON 응답값을 필드별로 검증할 수 있는 메소
                .andExpect(jsonPath("$.amount", is(amount)));
    }
}
