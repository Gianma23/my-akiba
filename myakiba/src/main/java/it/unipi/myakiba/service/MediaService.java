package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.media.AddReviewDto;
import it.unipi.myakiba.DTO.media.MediaCreationDto;
import it.unipi.myakiba.DTO.media.MediaIdNameDto;
import it.unipi.myakiba.DTO.media.ReviewDto;
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
        if(mediaType == MediaType.MANGA) {
            return mangaMongoRepository.findByNameContaining(name, pageable);
        } else if(mediaType == MediaType.ANIME) {
            return animeMongoRepository.findByNameContaining(name, pageable);
        } else {
            throw new IllegalArgumentException("Media type does not exist");
        }
    }
    //TODO: l'utente dovrebbe vedere la media degli score
    public MediaMongo getMediaById(MediaType mediaType, String mediaId) {
        if(mediaType == null)
            throw new IllegalArgumentException("Media type not specified");
        return mediaType == MediaType.MANGA ? mangaMongoRepository.findById(mediaId)
                .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId)) :
                animeMongoRepository.findById(mediaId)
                        .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));
    }

    public String addMedia(MediaCreationDto mediaCreationDto) {
        try {
            if (mediaCreationDto.getMediaType() == MediaType.MANGA) {
                MangaMongo newMangaMongo = new MangaMongo();
                newMangaMongo.setName(mediaCreationDto.getName());
                newMangaMongo.setStatus(mediaCreationDto.getStatus());
                newMangaMongo.setChapters(mediaCreationDto.getChapters());
                newMangaMongo.setSumScores(0);
                newMangaMongo.setNumScores(0);
                newMangaMongo.setGenres(mediaCreationDto.getGenres());
                newMangaMongo.setType(mediaCreationDto.getType());
                newMangaMongo.setAuthors(mediaCreationDto.getAuthors());
                newMangaMongo.setSynopsis(mediaCreationDto.getSynopsis());
                mangaMongoRepository.save(newMangaMongo);

                MangaNeo4j newMangaNeo4j = new MangaNeo4j();
                newMangaNeo4j.setId(newMangaMongo.getId());
                newMangaNeo4j.setName(mediaCreationDto.getName());
                newMangaNeo4j.setStatus(mediaCreationDto.getStatus());
                newMangaNeo4j.setChapters(mediaCreationDto.getChapters());
                newMangaNeo4j.setGenres(mediaCreationDto.getGenres());
                mangaNeo4jRepository.save(newMangaNeo4j);

                return "Successfully added manga";
            } else {
                AnimeMongo newAnimeMongo = new AnimeMongo();
                newAnimeMongo.setName(mediaCreationDto.getName());
                newAnimeMongo.setStatus(mediaCreationDto.getStatus());
                newAnimeMongo.setEpisodes(mediaCreationDto.getEpisodes());
                newAnimeMongo.setSumScores(0);
                newAnimeMongo.setNumScores(0);
                newAnimeMongo.setGenres(mediaCreationDto.getGenres());
                newAnimeMongo.setType(mediaCreationDto.getType());
                newAnimeMongo.setSource(mediaCreationDto.getSource());
                newAnimeMongo.setDuration(mediaCreationDto.getDuration());
                newAnimeMongo.setStudio(mediaCreationDto.getStudio());
                newAnimeMongo.setSynopsis(mediaCreationDto.getSynopsis());
                animeMongoRepository.save(newAnimeMongo);

                AnimeNeo4j newAnimeNeo4j = new AnimeNeo4j();
                newAnimeNeo4j.setId(newAnimeMongo.getId());
                newAnimeNeo4j.setName(mediaCreationDto.getName());
                newAnimeNeo4j.setStatus(mediaCreationDto.getStatus());
                newAnimeNeo4j.setEpisodes(mediaCreationDto.getEpisodes());
                newAnimeNeo4j.setGenres(mediaCreationDto.getGenres());
                animeNeo4jRepository.save(newAnimeNeo4j);
                return "Successfully added anime";
            }
        } catch (Exception e) {
            return "Error adding media";
        }
    }
    //TODO: ha senso che l'admin possa toccare review, numScores e sumScores?
    public String updateMedia(String mediaId, MediaType mediaType, Map<String, Object> updates) {
        if(mediaType == null)
            throw new IllegalArgumentException("Media type not specified");
        if(mediaType == MediaType.MANGA) {
            MangaMongo targetMongo = mangaMongoRepository.findById(mediaId)
                        .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));
            MangaNeo4j targetNeo4j = mangaNeo4jRepository.findById(mediaId)
                    .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));

        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    targetMongo.setName((String) value);
                    targetNeo4j.setName((String) value);
                    break;
                case "status":
                    targetMongo.setStatus((MediaStatus) value);
                    targetNeo4j.setStatus((MediaStatus) value);
                    break;
                case "chapters":
                    targetMongo.setChapters((int) value);
                    targetNeo4j.setChapters((int) value);
                    break;
                case "genres":
                    targetMongo.setGenres((List<String>) value);
                    targetNeo4j.setGenres((List<String>) value);
                    break;
                case "sumScores":
                    targetMongo.setSumScores((int) value);
                    break;
                case "numScores":
                    targetMongo.setNumScores((int) value);
                    break;
                case "type":
                    targetMongo.setType((String) value);
                    break;
                case "authors":
                    targetMongo.setAuthors((List<String>) value);
                    break;
                case "synopsis":
                    targetMongo.setSynopsis((String) value);
                    break;
                case "reviews":
                    targetMongo.setReviews((List<ReviewDto>) value);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported field: " + key);
            }
        });
            mangaMongoRepository.save(targetMongo);
            mangaNeo4jRepository.save(targetNeo4j);
        } else {
            AnimeMongo targetMongo = animeMongoRepository.findById(mediaId)
                    .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));
            AnimeNeo4j targetNeo4j = animeNeo4jRepository.findById(mediaId)
                    .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));

            updates.forEach((key, value) -> {
                switch (key) {
                    case "name":
                        targetMongo.setName((String) value);
                        targetNeo4j.setName((String) value);
                        break;
                    case "status":
                        targetMongo.setStatus((MediaStatus) value);
                        targetNeo4j.setStatus((MediaStatus) value);
                        break;
                    case "episodes":
                        targetMongo.setEpisodes((int) value);
                        targetNeo4j.setEpisodes((int) value);
                        break;
                    case "genres":
                        targetMongo.setGenres((List<String>) value);
                        targetNeo4j.setGenres((List<String>) value);
                        break;
                    case "sumScores":
                        targetMongo.setSumScores((int) value);
                        break;
                    case "numScores":
                        targetMongo.setNumScores((int) value);
                        break;
                    case "type":
                        targetMongo.setType((String) value);
                        break;
                    case "source":
                        targetMongo.setSource((String) value);
                        break;
                    case "duration":
                        targetMongo.setDuration((double) value);
                        break;
                    case "studio":
                        targetMongo.setStudio((String) value);
                        break;
                    case "synopsis":
                        targetMongo.setSynopsis((String) value);
                        break;
                    case "reviews":
                        targetMongo.setReviews((List<ReviewDto>) value);
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported field: " + key);
                }
            });
            animeMongoRepository.save(targetMongo);
            animeNeo4jRepository.save(targetNeo4j);
        }
        return "Successfully updated media";
    }

    public String deleteMedia(String mediaId, MediaType mediaType) {
        if(mediaType == null)
            throw new IllegalArgumentException("Media type not specified");
        if(mediaType == MediaType.MANGA) {
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

    //TODO: aggiornare sumScores e numScores
    public String addReview(MediaType mediaType, String mediaId, UserMongo user, AddReviewDto review) {
        ReviewDto newReview = new ReviewDto();
        newReview.setUserId(user.getId());
        newReview.setUsername(user.getUsername());
        newReview.setScore(review.getScore());
        newReview.setComment(review.getComment());
        newReview.setDate(LocalDate.now());

        if(mediaType == MediaType.MANGA) {
            MangaMongo targetMongo = mangaMongoRepository.findById(mediaId)
                    .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));
            List<ReviewDto> reviews = targetMongo.getReviews();
            reviews.add(newReview);
            targetMongo.setReviews(reviews);
            mangaMongoRepository.save(targetMongo);
        } else if (mediaType == MediaType.ANIME) {
            AnimeMongo targetMongo = animeMongoRepository.findById(mediaId)
                    .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));
            List<ReviewDto> reviews = targetMongo.getReviews();
            reviews.add(newReview);
            targetMongo.setReviews(reviews);
            animeMongoRepository.save(targetMongo);
        } else
            throw new IllegalArgumentException("Media type does not exist");
        return "Successfully added review";
    }

    //TODO: aggiornare sumScores e numScores
    public String deleteReview(String mediaId, String reviewId, MediaType mediaType) {
        if(mediaType == null)
            throw new IllegalArgumentException("Media type not specified");
        if(mediaType == MediaType.MANGA) {
            MangaMongo targetMongo = mangaMongoRepository.findById(mediaId)
                    .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));
            List<ReviewDto> reviews = targetMongo.getReviews();
            ReviewDto review = reviews.stream().filter(r -> r.getUserId().equals(reviewId))
                    .findFirst().orElseThrow(() -> new NoSuchElementException("Review not found with id: " + reviewId));
            reviews.remove(review);
            targetMongo.setReviews(reviews);
            mangaMongoRepository.save(targetMongo);
        } else {
            AnimeMongo targetMongo = animeMongoRepository.findById(mediaId)
                    .orElseThrow(() -> new NoSuchElementException("Media not found with id: " + mediaId));
            List<ReviewDto> reviews = targetMongo.getReviews();
            ReviewDto review = reviews.stream().filter(r -> r.getUserId().equals(reviewId))
                    .findFirst().orElseThrow(() -> new NoSuchElementException("Review not found with id: " + reviewId));
            reviews.remove(review);
            targetMongo.setReviews(reviews);
            animeMongoRepository.save(targetMongo);
        }
        return "Successfully deleted review";
    }
}
