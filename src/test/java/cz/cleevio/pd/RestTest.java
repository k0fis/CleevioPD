package cz.cleevio.pd;

import cz.cleevio.pd.dto.WatchDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.AssertionErrors;

import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class RestTest {

    @Autowired
    private TestRestTemplate template;

    String json_1 = "{\n" +
            "\"title\": \"Prim\",\n" +
            "\"price\": \"250001\", \n" +
            "\"description\": \"A watch with a water fountain picture\", \"fountain\": \"R0lGODlhAQABAIAAAAUEBA==\" \n" +
            "}";

    String json_2 = "{\n" +
            "\"title\": \"Prim\",\n" +
            "\"price\": \"250002\", \n" +
            "\"description\": \"A watch with a water fountain picture\", \"fountain\": \"R0lGODlhAQABAIAAAAUEBA\" \n" +
            "}";

    String xml_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "<watchdto>\n" +
            "  <title>Prim</title>\n" +
            "  <price>250003</price>\n" +
            "  <description>A watch with a water fountain picture</description>\n" +
            "  <fountain>R0lGODlhAQABAIAAAAUEBA==</fountain>\n" +
            "</watchdto>";

    WatchDto testWatch_1 = new WatchDto("Prim", 250_004, "A watch with a water fountain picture",
            "R0lGODlhAQABAIAAAAUEBA==");
    WatchDto testWatch_2 = new WatchDto("Prim", 250_005, "A watch with a water fountain picture",
            "R0lGODlhAQABAIAAAAUEBA");

    @Test
    void uploads() {
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<Void> re;

        // CREATED
        re = template.postForEntity("/", testWatch_1, Void.class);
        AssertionErrors.assertEquals("Test 1 - 201 CREATED", HttpStatus.CREATED.value(), re.getStatusCodeValue());

        // NOT CREATED - bad Base64 string
        re = template.postForEntity("/", testWatch_2, Void.class);
        AssertionErrors.assertEquals("Test 2 - Bad base 64 string", HttpStatus.BAD_REQUEST.value(), re.getStatusCodeValue());

        log.info("Test send JSON");
        headers.setContentType(MediaType.APPLICATION_JSON);
        re = template.exchange("/", HttpMethod.POST, new HttpEntity<>(json_1, headers), Void.class);
        AssertionErrors.assertEquals("Test 3 - JSON 201 CREATED", HttpStatus.CREATED.value(), re.getStatusCodeValue());

        re = template.exchange("/", HttpMethod.POST, new HttpEntity<>(json_2, headers), Void.class);
        AssertionErrors.assertEquals("Test 4 - JSON bad bas64", HttpStatus.BAD_REQUEST.value(), re.getStatusCodeValue());

		log.info("Test XML");
        headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		re = template.exchange("/", HttpMethod.POST, new HttpEntity<>(xml_1, headers), Void.class);
		AssertionErrors.assertEquals("Test 5 - XML 201 CREATED", HttpStatus.CREATED.value(), re.getStatusCodeValue() );

        log.info("Test get by ID");
        ResponseEntity<WatchDto> rew;
        rew = template.getForEntity("/byId/1", WatchDto.class);
        log.info("get: {}", rew);
        AssertionErrors.assertEquals("Test 6 - get by id", HttpStatus.OK.value(), rew.getStatusCodeValue());

        log.info("Test get all");

        ResponseEntity<List<WatchDto>> repw;
        repw = template.exchange(
                "/all?page=1&size=5",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        log.info("get: {}", repw);
        AssertionErrors.assertEquals("Test 7 - get all", HttpStatus.OK.value(), repw.getStatusCodeValue());

    }


}
