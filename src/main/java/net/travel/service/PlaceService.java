package net.travel.service;

import net.travel.dto.PlaceDto;
import net.travel.dto.SearchDto;
import net.travel.model.Place;
import net.travel.model.PlaceImage;
import net.travel.util.model.TourData;
import net.travel.util.model.TourDetailData;
import net.travel.util.search.SearchParam;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlaceService {

    List<TourData<Place>> getByHighestRating(int userId, Pageable pageable);

    List<PlaceDto> getAllByDto();

    SearchDto<PlaceDto> getByParams(SearchParam searchParam, Pageable pageable, int userId);

    TourDetailData<Place, PlaceImage> getDetailById(int id, int userId);

    boolean existsById(int placeId);
}