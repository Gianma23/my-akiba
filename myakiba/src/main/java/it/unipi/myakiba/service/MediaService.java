package it.unipi.myakiba.service;

import it.unipi.myakiba.DTO.MediaCreationDto;
import it.unipi.myakiba.enumerator.MediaType;
import it.unipi.myakiba.model.AnimeNeo4j;
import it.unipi.myakiba.model.MangaMongo;
import it.unipi.myakiba.model.AnimeMongo;
import it.unipi.myakiba.model.MangaNeo4j;
import it.unipi.myakiba.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

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
    @Autowired
    private MongoTemplate mongoTemplate;

    public String getMedia(String type) throws Exception {
        return null;
    }

    public String addMedia(MediaCreationDto mediaCreationDto) throws Exception {
        if(mediaCreationDto.getMediaType() == MediaType.MANGA) {
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
    }
}
