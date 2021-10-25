package cz.cleevio.pd.service;

import cz.cleevio.pd.dto.WatchDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
public class WatchRest {

    private final WatchService watchService;

    private final Pattern checkBase64 = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$");

    public WatchRest(WatchService watchService) {
        this.watchService = watchService;
    }


    @GetMapping(value = "/byId/{id}")
    public WatchDto getById(@PathVariable Long id) {
        log.debug("getById {}", id);
        WatchDto watchDto = watchService.getWatchById(id);
        if (watchDto == null) {
            log.warn("Image {} is not in store", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return watchDto;
    }

    @GetMapping(value = "/all", params = {"page", "size"})
    public List<WatchDto> getAll(
            @RequestParam int page,
            @RequestParam int size) {
        log.debug("getAll page: {} size: {}", page, size);
        page = page-1;
        if (page < 0) {
            page = 0;
        }
        if (size < 0) {
            size = 1;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<WatchDto> watchDto = watchService.getWatches(pageable);
        return watchDto.getContent();
    }

    @PostMapping
    public void upload(@RequestBody WatchDto watchDto, HttpServletResponse response) {
        log.debug("upload {}", watchDto);

        // validate input object
        if (watchDto.getFountain() == null) {
            log.error("Cannot save watches title: {}, empty fountain", watchDto.getTitle());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }
        Matcher matcher = checkBase64.matcher(watchDto.getFountain());
        if (!matcher.find()) {
            log.error("Cannot save watches title: {}, invalid image", watchDto.getTitle());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        if (watchService.saveWatch(watchDto)) {
            log.debug("Watch saved");
            response.setStatus(HttpStatus.CREATED.value());
            return;
        }

        log.error("Cannot save watches title: {}", watchDto.getTitle());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }



}
