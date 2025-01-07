package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.media.*;
import it.unipi.myakiba.enumerator.MediaStatus;
import it.unipi.myakiba.enumerator.MediaType;
import it.unipi.myakiba.model.*;
import it.unipi.myakiba.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class MediaService {
    private final MangaNeo4jRepository mangaNeo4jRepository;
    private final AnimeNeo4jRepository animeNeo4jRepository;
    private final MangaMongoRepository mangaMongoRepository;
    private final AnimeMongoRepository animeMongoRepository;

    @Autowired
    public MediaService(MangaNeo4jRepository mangaNeo4jRepository, AnimeNeo4jRepository animeNeo4jRepository, MangaMongoRepository mangaMongoRepository, AnimeMongoRepository animeMongoRepository) {
        this.mangaNeo4jRepository = mangaNeo4jRepository;
        this.animeNeo4jRepository = animeNeo4jRepository;
        this.mangaMongoRepository = mangaMongoRepository;
        this.animeMongoRepository = animeMongoRepository;
    }

    /* ================================ MEDIA CRUD ================================ */

    public Slice<MediaIdNameDto> browseMedia(MediaType mediaType, String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (mediaType == MediaType.MANGA) {
            return mangaMongoRepository.findByNameContaining(name, pageable);
        } else {
            return animeMongoRepository.findByNameContaining(name, pageable);
        }
    }

    public MediaDetailsDto getMediaById(MediaType mediaType, String mediaId) {
        MediaMongo media = mediaType == MediaType.MANGA ? mangaMongoRepository.findById(mediaId)
                .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId)) :
                animeMongoRepository.findById(mediaId)
                        .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));

        if (mediaType == MediaType.MANGA) {
            MangaMongo manga = (MangaMongo) media;
            return MangaDetailsDto.builder()
                    .name(manga.getName())
                    .status(manga.getStatus())
                    .avgScore(manga.getNumScores() == 0 ? 0 : (double) manga.getSumScores() / manga.getNumScores())
                    .genres(manga.getGenres())
                    .synopsis(manga.getSynopsis())
                    .type(manga.getType())
                    .chapters(manga.getChapters())
                    .authors(manga.getAuthors())
                    .build();
        } else {
            AnimeMongo anime = (AnimeMongo) media;
            return AnimeDetailsDto.builder()
                    .name(anime.getName())
                    .status(anime.getStatus())
                    .avgScore(anime.getNumScores() == 0 ? 0 : (double) anime.getSumScores() / anime.getNumScores())
                    .genres(anime.getGenres())
                    .synopsis(anime.getSynopsis())
                    .type(anime.getType())
                    .episodes(anime.getEpisodes())
                    .source(anime.getSource())
                    .duration(anime.getDuration())
                    .studio(anime.getStudio())
                    .build();
        }
    }

    public String addMedia(MediaType mediaType, MediaCreationDto mediaCreationDto) {
        if (mediaType == MediaType.MANGA) {
            MangaCreationDto mangaCreationDto = (MangaCreationDto) mediaCreationDto;
            MangaMongo newMangaMongo = new MangaMongo();
            newMangaMongo.setName(mangaCreationDto.getName());
            newMangaMongo.setStatus(mangaCreationDto.getStatus());
            newMangaMongo.setChapters(mangaCreationDto.getChapters());
            newMangaMongo.setSumScores(0);
            newMangaMongo.setNumScores(0);
            newMangaMongo.setGenres(mangaCreationDto.getGenres());
            newMangaMongo.setType(mangaCreationDto.getType());
            newMangaMongo.setAuthors(mangaCreationDto.getAuthors());
            newMangaMongo.setSynopsis(mangaCreationDto.getSynopsis());
            mangaMongoRepository.save(newMangaMongo);

            MangaNeo4j newMangaNeo4j = new MangaNeo4j();
            newMangaNeo4j.setId(newMangaMongo.getId());
            newMangaNeo4j.setName(mangaCreationDto.getName());
            newMangaNeo4j.setStatus(mangaCreationDto.getStatus());
            newMangaNeo4j.setChapters(mangaCreationDto.getChapters());
            newMangaNeo4j.setGenres(mangaCreationDto.getGenres());
            mangaNeo4jRepository.save(newMangaNeo4j);

            return "Successfully added manga";
        } else {
            AnimeCreationDto animeCreationDto = (AnimeCreationDto) mediaCreationDto;
            AnimeMongo newAnimeMongo = new AnimeMongo();
            newAnimeMongo.setName(animeCreationDto.getName());
            newAnimeMongo.setStatus(animeCreationDto.getStatus());
            newAnimeMongo.setEpisodes(animeCreationDto.getEpisodes());
            newAnimeMongo.setSumScores(0);
            newAnimeMongo.setNumScores(0);
            newAnimeMongo.setGenres(animeCreationDto.getGenres());
            newAnimeMongo.setType(animeCreationDto.getType());
            newAnimeMongo.setSource(animeCreationDto.getSource());
            newAnimeMongo.setDuration(animeCreationDto.getDuration());
            newAnimeMongo.setStudio(animeCreationDto.getStudio());
            newAnimeMongo.setSynopsis(animeCreationDto.getSynopsis());
            animeMongoRepository.save(newAnimeMongo);

            AnimeNeo4j newAnimeNeo4j = new AnimeNeo4j();
            newAnimeNeo4j.setId(newAnimeMongo.getId());
            newAnimeNeo4j.setName(animeCreationDto.getName());
            newAnimeNeo4j.setStatus(animeCreationDto.getStatus());
            newAnimeNeo4j.setEpisodes(animeCreationDto.getEpisodes());
            newAnimeNeo4j.setGenres(animeCreationDto.getGenres());
            animeNeo4jRepository.save(newAnimeNeo4j);
            return "Successfully added anime";
        }
    }

    public String updateMedia(String mediaId, MediaType mediaType,  MediaUpdateDto updates) {
        if (mediaType == MediaType.MANGA) {
            MangaMongo targetMongo = mangaMongoRepository.findById(mediaId)
                    .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));
            MangaNeo4j targetNeo4j = mangaNeo4jRepository.findById(mediaId)
                    .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));

            MangaUpdateDto mangaUpdateDto = (MangaUpdateDto) updates;
            if (mangaUpdateDto.getName() != null) {
                targetMongo.setName(mangaUpdateDto.getName());
                targetNeo4j.setName(mangaUpdateDto.getName());
            }
            if (mangaUpdateDto.getStatus() != null) {
                targetMongo.setStatus(mangaUpdateDto.getStatus());
                targetNeo4j.setStatus(mangaUpdateDto.getStatus());
            }
            if (mangaUpdateDto.getChapters() != 0) {
                targetMongo.setChapters(mangaUpdateDto.getChapters());
                targetNeo4j.setChapters(mangaUpdateDto.getChapters());
            }
            if (mangaUpdateDto.getGenres() != null) {
                targetMongo.setGenres(mangaUpdateDto.getGenres());
                targetNeo4j.setGenres(mangaUpdateDto.getGenres());
            }
            if (mangaUpdateDto.getAuthors() != null) {
                targetMongo.setAuthors(mangaUpdateDto.getAuthors());
            }
            if (mangaUpdateDto.getSynopsis() != null) {
                targetMongo.setSynopsis(mangaUpdateDto.getSynopsis());
            }
            if (mangaUpdateDto.getType() != null) {
                targetMongo.setType(mangaUpdateDto.getType());
            }
            mangaMongoRepository.save(targetMongo);
            mangaNeo4jRepository.save(targetNeo4j);
        } else {
            AnimeMongo targetMongo = animeMongoRepository.findById(mediaId)
                    .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));
            AnimeNeo4j targetNeo4j = animeNeo4jRepository.findById(mediaId)
                    .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));

            AnimeUpdateDto animeUpdateDto = (AnimeUpdateDto) updates;
            if (animeUpdateDto.getName() != null) {
                targetMongo.setName(animeUpdateDto.getName());
                targetNeo4j.setName(animeUpdateDto.getName());
            }
            if (animeUpdateDto.getStatus() != null) {
                targetMongo.setStatus(animeUpdateDto.getStatus());
                targetNeo4j.setStatus(animeUpdateDto.getStatus());
            }
            if (animeUpdateDto.getEpisodes() != 0) {
                targetMongo.setEpisodes(animeUpdateDto.getEpisodes());
                targetNeo4j.setEpisodes(animeUpdateDto.getEpisodes());
            }
            if (animeUpdateDto.getGenres() != null) {
                targetMongo.setGenres(animeUpdateDto.getGenres());
                targetNeo4j.setGenres(animeUpdateDto.getGenres());
            }
            if (animeUpdateDto.getSynopsis() != null) {
                targetMongo.setSynopsis(animeUpdateDto.getSynopsis());
            }
            if (animeUpdateDto.getType() != null) {
                targetMongo.setType(animeUpdateDto.getType());
            }
            if (animeUpdateDto.getSource() != null) {
                targetMongo.setSource(animeUpdateDto.getSource());
            }
            if (animeUpdateDto.getDuration() != 0) {
                targetMongo.setDuration(animeUpdateDto.getDuration());
            }
            if (animeUpdateDto.getStudio() != null) {
                targetMongo.setStudio(animeUpdateDto.getStudio());
            }

            animeMongoRepository.save(targetMongo);
            animeNeo4jRepository.save(targetNeo4j);
        }
        return "Successfully updated media";
    }

    public String deleteMedia(String mediaId, MediaType mediaType) {
        if (mediaType == MediaType.MANGA) {
            MangaMongo targetMongo = mangaMongoRepository.findById(mediaId)
                    .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));
            MangaNeo4j targetNeo4j = mangaNeo4jRepository.findById(mediaId)
                    .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));
            mangaMongoRepository.delete(targetMongo);
            mangaNeo4jRepository.delete(targetNeo4j);
        } else {
            AnimeMongo targetMongo = animeMongoRepository.findById(mediaId)
                    .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));
            AnimeNeo4j targetNeo4j = animeNeo4jRepository.findById(mediaId)
                    .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));
            animeMongoRepository.delete(targetMongo);
            animeNeo4jRepository.delete(targetNeo4j);
        }
        return "Successfully deleted media";
    }

    /* ================================ REVIEWS ================================ */

    public String addReview(MediaType mediaType, String mediaId, UserMongo user, AddReviewDto review) {
        ReviewDto newReview = new ReviewDto();
        newReview.setUserId(user.getId());
        newReview.setUsername(user.getUsername());
        newReview.setScore(review.getScore());
        newReview.setComment(review.getComment());
        newReview.setDate(LocalDate.now());

        if (mediaType == MediaType.MANGA) {
            MangaMongo targetMongo = mangaMongoRepository.findById(mediaId)
                    .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));
            List<ReviewDto> reviews = targetMongo.getReviews();
            reviews.add(newReview);
            targetMongo.setReviews(reviews);
            targetMongo.setSumScores(targetMongo.getSumScores() + review.getScore());
            targetMongo.setNumScores(targetMongo.getNumScores() + 1);
            mangaMongoRepository.save(targetMongo);
        } else {
            AnimeMongo targetMongo = animeMongoRepository.findById(mediaId)
                    .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));
            List<ReviewDto> reviews = targetMongo.getReviews();
            reviews.add(newReview);
            targetMongo.setReviews(reviews);
            targetMongo.setSumScores(targetMongo.getSumScores() + review.getScore());
            targetMongo.setNumScores(targetMongo.getNumScores() + 1);
            animeMongoRepository.save(targetMongo);
        }
        return "Successfully added review";
    }

    public String deleteReview(String mediaId, String reviewId, MediaType mediaType) {
        if (mediaType == MediaType.MANGA) {
            MangaMongo targetMongo = mangaMongoRepository.findById(mediaId)
                    .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));
            List<ReviewDto> reviews = targetMongo.getReviews();
            ReviewDto review = reviews.stream().filter(r -> r.getUserId().equals(reviewId))
                    .findFirst().orElseThrow(() -> new NoSuchElementException("Review not found with id: " + reviewId));
            reviews.remove(review);
            targetMongo.setReviews(reviews);
            targetMongo.setSumScores(targetMongo.getSumScores() - review.getScore());
            targetMongo.setNumScores(targetMongo.getNumScores() - 1);
            mangaMongoRepository.save(targetMongo);
        } else {
            AnimeMongo targetMongo = animeMongoRepository.findById(mediaId)
                    .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));
            List<ReviewDto> reviews = targetMongo.getReviews();
            ReviewDto review = reviews.stream().filter(r -> r.getUserId().equals(reviewId))
                    .findFirst().orElseThrow(() -> new NoSuchElementException("Review not found with id: " + reviewId));
            reviews.remove(review);
            targetMongo.setReviews(reviews);
            targetMongo.setSumScores(targetMongo.getSumScores() - review.getScore());
            targetMongo.setNumScores(targetMongo.getNumScores() - 1);
            animeMongoRepository.save(targetMongo);
        }
        return "Successfully deleted review";
    }
}
