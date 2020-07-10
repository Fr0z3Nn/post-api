package com.reckue.post.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reckue.post.PostServiceApplicationTests;
import com.reckue.post.models.Post;
import com.reckue.post.models.types.LangType;
import com.reckue.post.models.types.NodeType;
import com.reckue.post.models.types.StatusType;
import com.reckue.post.repositories.PostRepository;
import com.reckue.post.transfers.NodeRequest;
import com.reckue.post.transfers.PostRequest;
import com.reckue.post.transfers.PostResponse;
import com.reckue.post.transfers.nodes.audio.AudioNodeRequest;
import com.reckue.post.transfers.nodes.code.CodeNodeRequest;
import com.reckue.post.transfers.nodes.image.ImageNodeRequest;
import com.reckue.post.transfers.nodes.list.ListNodeRequest;
import com.reckue.post.transfers.nodes.poll.PollNodeRequest;
import com.reckue.post.transfers.nodes.text.TextNodeRequest;
import com.reckue.post.transfers.nodes.video.VideoNodeRequest;
import com.reckue.post.utils.converters.PostConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.reckue.post.utils.converters.PostConverter.convert;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Class PostControllerIntTest is the integration type of test.
 *
 * @author Kamila Meshcheryakova
 */
@AutoConfigureMockMvc
public class PostControllerIntTest extends PostServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostController postController;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        postRepository.deleteAll();

        postRepository.save(Post.builder()
                .id("4")
                .title("oracle")
                .source("Github.com")
                .published(1491379425L)
                .changed(1491465825L)
                .status(StatusType.DELETED)
                .userId("daria")
                .build());
        postRepository.save(Post.builder()
                .id("1")
                .title("string")
                .source("Wikipedia.com")
                .published(1591379425L)
                .changed(1591465825L)
                .status(StatusType.ACTIVE)
                .userId("egnaf")
                .build());
        postRepository.save(Post.builder()
                .id("3")
                .title("pupil")
                .source("Google.com")
                .published(1601920225L)
                .changed(1602006625L)
                .status(StatusType.BANNED)
                .userId("camelya")
                .build());
        postRepository.save(Post.builder()
                .id("2")
                .title("title")
                .source("Habr.com")
                .published(1701920225L)
                .changed(1702006625L)
                .status(StatusType.MODERATED)
                .userId("hardele")
                .build());
    }

    @Test
    public void loadTest() {
        assertThat(postController).isNotNull();
    }

    @Test
    public void findByIdTest() throws Exception {
        PostResponse expected = PostConverter.convert(postRepository.findAll().get(0));

        PostResponse actual = objectMapper.readValue(this.mockMvc
                .perform(get("/posts/" + expected.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(), PostResponse.class);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void findAllSortedByIdDescTest() throws Exception {
        List<PostResponse> expected = postRepository.findAll().stream()
                .map(PostConverter::convert)
                .sorted(Comparator.comparing(PostResponse::getId).reversed())
                .limit(2)
                .collect(Collectors.toList());

        List<PostResponse> actual = objectMapper.readValue(this.mockMvc
                .perform(get("/posts?desc=true&limit=2&offset=0&sort=id"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void findAllSortedByTitleAscTest() throws Exception {
        List<PostResponse> expected = postRepository.findAll().stream()
                .map(PostConverter::convert)
                .sorted(Comparator.comparing(PostResponse::getTitle))
                .limit(3)
                .collect(Collectors.toList());

        List<PostResponse> actual = objectMapper.readValue(this.mockMvc
                .perform(get("/posts?desc=false&limit=3&offset=0&sort=title"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void findAllSortedBySourceDescTest() throws Exception {
        List<PostResponse> expected = postRepository.findAll().stream()
                .map(PostConverter::convert)
                .sorted(Comparator.comparing(PostResponse::getSource).reversed())
                .limit(3)
                .collect(Collectors.toList());

        List<PostResponse> actual = objectMapper.readValue(this.mockMvc
                .perform(get("/posts?desc=true&limit=3&offset=0&sort=source"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void findAllSortedByPublishedAscTest() throws Exception {
        List<PostResponse> expected = postRepository.findAll().stream()
                .map(PostConverter::convert)
                .sorted(Comparator.comparing(PostResponse::getPublished))
                .limit(2)
                .collect(Collectors.toList());

        List<PostResponse> actual = objectMapper.readValue(this.mockMvc
                .perform(get("/posts?desc=false&limit=2&offset=0&sort=published"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void findAllSortedByChangedDescTest() throws Exception {
        List<PostResponse> expected = postRepository.findAll().stream()
                .map(PostConverter::convert)
                .sorted(Comparator.comparing(PostResponse::getChanged).reversed())
                .limit(2)
                .skip(1)
                .collect(Collectors.toList());

        List<PostResponse> actual = objectMapper.readValue(this.mockMvc
                .perform(get("/posts?desc=true&limit=2&offset=1&sort=changed"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void findAllSortedByStatusAscTest() throws Exception {
        List<PostResponse> expected = postRepository.findAll().stream()
                .map(PostConverter::convert)
                .sorted(Comparator.comparing(PostResponse::getStatus))
                .limit(2)
                .skip(1)
                .collect(Collectors.toList());

        List<PostResponse> actual = objectMapper.readValue(this.mockMvc
                .perform(get("/posts?desc=false&limit=2&offset=1&sort=status"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void createTest() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .title("news")
                .nodes(null)
                .source("Habr.com")
                .tags(null)
                .userId("camelya")
                .published(1591465825L)
                .changed(1591465825L)
                .status(StatusType.MODERATED)
                .build();

        PostResponse actual = objectMapper.readValue(this.mockMvc
                .perform(MockMvcRequestBuilders.post("/posts")
                        .content(objectMapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(), PostResponse.class);

        PostResponse expected = PostConverter.convert(PostConverter.convert(postRequest));
        expected.setId(actual.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void createTestWithPollNode() throws Exception {
        NodeRequest pollNode = NodeRequest.builder()
                .postId("1")
                .type(NodeType.POLL)
                .node(PollNodeRequest.builder()
                    .title("news")
                    .items(List.of("One", "Two"))
                    .build())
                .build();
        PostRequest postRequest = PostRequest.builder()
                .title("news")
                .nodes(List.of(pollNode))
                .source("Habr.com")
                .tags(null)
                .userId("camelya")
                .published(1591465825L)
                .changed(1591465825L)
                .status(StatusType.MODERATED)
                .build();

        PostResponse actual = objectMapper.readValue(this.mockMvc
                .perform(MockMvcRequestBuilders.post("/posts")
                        .content(objectMapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(), PostResponse.class);
        PostResponse expected = convert(convert(postRequest));
        expected.setId(actual.getId());
        expected.getNodes().get(0).setId(actual.getNodes().get(0).getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void createTestWithAudioNode() throws Exception {
        NodeRequest audioNode = NodeRequest.builder()
                .postId("1")
                .type(NodeType.AUDIO)
                .node(AudioNodeRequest.builder()
                        .audioUrl("url")
                        .build())
                .build();
        PostRequest postRequest = PostRequest.builder()
                .title("news")
                .nodes(List.of(audioNode))
                .source("Habr.com")
                .tags(null)
                .userId("camelya")
                .published(1591465825L)
                .changed(1591465825L)
                .status(StatusType.MODERATED)
                .build();

        PostResponse actual = objectMapper.readValue(this.mockMvc
                .perform(MockMvcRequestBuilders.post("/posts")
                        .content(objectMapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(), PostResponse.class);
        PostResponse expected = convert(convert(postRequest));
        expected.setId(actual.getId());
        expected.getNodes().get(0).setId(actual.getNodes().get(0).getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void createTestWithCodeNode() throws Exception {
        NodeRequest codeNode = NodeRequest.builder()
                .postId("1")
                .type(NodeType.CODE)
                .node(CodeNodeRequest.builder()
                        .language(LangType.JAVA)
                        .content("main")
                        .build())
                .build();
        PostRequest postRequest = PostRequest.builder()
                .title("news")
                .nodes(List.of(codeNode))
                .source("Habr.com")
                .tags(null)
                .userId("camelya")
                .published(1591465825L)
                .changed(1591465825L)
                .status(StatusType.MODERATED)
                .build();

        PostResponse actual = objectMapper.readValue(this.mockMvc
                .perform(MockMvcRequestBuilders.post("/posts")
                        .content(objectMapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(), PostResponse.class);
        PostResponse expected = convert(convert(postRequest));
        expected.setId(actual.getId());
        expected.getNodes().get(0).setId(actual.getNodes().get(0).getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void createTestWithTextNode() throws Exception {
        NodeRequest textNode = NodeRequest.builder()
                .postId("1")
                .type(NodeType.TEXT)
                .node(TextNodeRequest.builder()
                        .content("Just Text")
                        .build())
                .build();
        PostRequest postRequest = PostRequest.builder()
                .title("news")
                .nodes(List.of(textNode))
                .source("Habr.com")
                .tags(null)
                .userId("camelya")
                .published(1591465825L)
                .changed(1591465825L)
                .status(StatusType.MODERATED)
                .build();

        PostResponse actual = objectMapper.readValue(this.mockMvc
                .perform(MockMvcRequestBuilders.post("/posts")
                        .content(objectMapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(), PostResponse.class);
        PostResponse expected = convert(convert(postRequest));
        expected.setId(actual.getId());
        expected.getNodes().get(0).setId(actual.getNodes().get(0).getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void createTestWithImageNode() throws Exception {
        NodeRequest imageNode = NodeRequest.builder()
                .postId("1")
                .type(NodeType.IMAGE)
                .node(ImageNodeRequest.builder()
                        .imageUrl("url")
                        .build())
                .build();
        PostRequest postRequest = PostRequest.builder()
                .title("news")
                .nodes(List.of(imageNode))
                .source("Habr.com")
                .tags(null)
                .userId("camelya")
                .published(1591465825L)
                .changed(1591465825L)
                .status(StatusType.MODERATED)
                .build();

        PostResponse actual = objectMapper.readValue(this.mockMvc
                .perform(MockMvcRequestBuilders.post("/posts")
                        .content(objectMapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(), PostResponse.class);
        PostResponse expected = convert(convert(postRequest));
        expected.setId(actual.getId());
        expected.getNodes().get(0).setId(actual.getNodes().get(0).getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void createTestWithListNode() throws Exception {
        NodeRequest listNode = NodeRequest.builder()
                .postId("1")
                .type(NodeType.LIST)
                .node(ListNodeRequest.builder()
                        .content(List.of("List"))
                        .build())
                .build();
        PostRequest postRequest = PostRequest.builder()
                .title("news")
                .nodes(List.of(listNode))
                .source("Habr.com")
                .tags(null)
                .userId("camelya")
                .published(1591465825L)
                .changed(1591465825L)
                .status(StatusType.MODERATED)
                .build();

        PostResponse actual = objectMapper.readValue(this.mockMvc
                .perform(MockMvcRequestBuilders.post("/posts")
                        .content(objectMapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(), PostResponse.class);
        PostResponse expected = convert(convert(postRequest));
        expected.setId(actual.getId());
        expected.getNodes().get(0).setId(actual.getNodes().get(0).getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void createTestWithVideoNode() throws Exception {
        NodeRequest videoNode = NodeRequest.builder()
                .postId("1")
                .type(NodeType.VIDEO)
                .node(VideoNodeRequest.builder()
                        .videoUrl("url")
                        .build())
                .build();
        PostRequest postRequest = PostRequest.builder()
                .title("news")
                .nodes(List.of(videoNode))
                .source("Habr.com")
                .tags(null)
                .userId("camelya")
                .published(1591465825L)
                .changed(1591465825L)
                .status(StatusType.MODERATED)
                .build();

        PostResponse actual = objectMapper.readValue(this.mockMvc
                .perform(MockMvcRequestBuilders.post("/posts")
                        .content(objectMapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(), PostResponse.class);
        PostResponse expected = convert(convert(postRequest));
        expected.setId(actual.getId());
        expected.getNodes().get(0).setId(actual.getNodes().get(0).getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void updateTest() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .title("title")
                .nodes(null)
                .source("Habr.com")
                .tags(null)
                .userId("hardele")
                .published(1701920225L)
                .changed(1802006625L)
                .status(StatusType.ACTIVE)
                .build();

        PostResponse actual = objectMapper.readValue(this.mockMvc
                .perform(MockMvcRequestBuilders.put("/posts/2")
                        .content(objectMapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString(), PostResponse.class);

        PostResponse expected = PostConverter.convert(PostConverter.convert(postRequest));
        expected.setId(actual.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void deleteByIdTest() throws Exception {
        int size = postRepository.findAll().size();
        this.mockMvc.perform(delete("/posts/" + postRepository.findAll().get(0).getId()))
                .andDo(print())
                .andExpect(status().isOk());
        Assertions.assertEquals(size - 1, postRepository.findAll().size());
    }
}
