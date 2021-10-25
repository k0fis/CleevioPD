package cz.cleevio.pd.service;

import cz.cleevio.pd.db.Watch;
import cz.cleevio.pd.dto.WatchDto;
import cz.cleevio.pd.repository.WatchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Slf4j
@Service
public class WatchService {

    private final WatchRepository watchRepository;

    private final Base64.Encoder encoder;
    private final Base64.Decoder decoder;

    public WatchService(WatchRepository watchRepository) {
        this.watchRepository = watchRepository;
        this.encoder = Base64.getEncoder();
        this.decoder = Base64.getDecoder();
    }

    private WatchDto toDto(Watch watch) {
        return new WatchDto(
                watch.getTitle(),
                watch.getPrice(),
                watch.getDescription(),
                encoder.encodeToString(watch.getFountain()));
    }

    public WatchDto getWatchById(Long id) {
        log.debug("getWatchById({})", id);
        Watch watch = watchRepository.findById(id).orElse(null);
        if (watch == null) {
            log.debug("getWatchById({}) -> null", id);
            return null;
        }
        log.debug("getWatchById({}) -> {}", id, watch);
        return toDto(watch);
    }

    public Page<WatchDto> getWatches(Pageable pageable) {
        log.debug("getWatches ( p: {}, s: {})", pageable.getPageNumber(), pageable.getPageSize());
        Page<Watch> watches = watchRepository.findAll(pageable);
        return watches.map(this::toDto);
    }

    public boolean saveWatch(WatchDto watchDto) {
        log.debug("saveWatch");

        Watch watch = new Watch();
        watch.setTitle(watchDto.getTitle());
        watch.setPrice(watchDto.getPrice());
        watch.setDescription(watchDto.getDescription());
        watch.setFountain(decoder.decode(watchDto.getFountain()));

        try {
            Watch w = watchRepository.save(watch);
            log.debug("saveWatch id: {} ", w.getId());
            return true;
        } catch (IllegalArgumentException iae) {
            log.debug("Cannot save watch");
            return false;
        }
    }
}
