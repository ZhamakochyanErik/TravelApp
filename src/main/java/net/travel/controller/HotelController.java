package net.travel.controller;

import net.travel.config.security.CurrentUser;
import net.travel.dto.HotelDto;
import net.travel.dto.PlaceDto;
import net.travel.dto.ReviewAddDto;
import net.travel.dto.SearchDto;
import net.travel.form.ReviewForm;
import net.travel.model.Hotel;
import net.travel.model.HotelImage;
import net.travel.model.Review;
import net.travel.repository.ReviewRepository;
import net.travel.service.*;
import net.travel.util.AuthenticationUtil;
import net.travel.util.DataUtil;
import net.travel.util.NumberUtil;
import net.travel.util.TemplateUtil;
import net.travel.util.model.TourDetailData;
import net.travel.util.search.SearchModelType;
import net.travel.util.search.SearchParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
public class HotelController {

    private static final Logger LOGGER = LogManager.getLogger();
    @Autowired
    private HotelService hotelService;

    @Autowired
    private NumberUtil numberUtil;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    @Autowired
    private DataUtil dataUtil;

    @GetMapping("/hotels")
    public String hotelsTemplate(@AuthenticationPrincipal CurrentUser currentUser,
                                 Model model){
        authenticationUtil.addUserDataInModel(currentUser,model);
        return TemplateUtil.HOTELS;
    }

    @GetMapping("/hotels/search")
    public @ResponseBody
    ResponseEntity hotelsSearch(@RequestParam(value = "name",required = false,defaultValue = "-1")String name,
                                @RequestParam(value = "place",required = false,defaultValue = "-1")String place,
                                @RequestParam(value = "roomsCount",required = false,defaultValue = "-1")String roomsCount,
                                @RequestParam(value = "region",required = false,defaultValue = "-1")String region,
                                @RequestParam(value = "city",required = false,defaultValue = "-1")String city,
                                @RequestParam(value = "order",required = false,defaultValue = "new")String order,
                                Pageable pageable,
                                @AuthenticationPrincipal CurrentUser currentUser) {
        SearchParam searchParam = SearchParam
                .builder()
                .modelType(SearchModelType.HOTEL)
                .name(name)
                .place(place)
                .roomsCount(roomsCount)
                .price("-1")
                .region(region)
                .city(city)
                .order(order)
                .userId(-1)
                .build();
        boolean isUserExist = currentUser != null;
        SearchDto<HotelDto> searchDto = dataUtil.getSearchModel(searchParam,
                pageable,isUserExist ? currentUser.getUser().getId() : -1,hotelService::getByParams);
        return ResponseEntity
                .ok(searchDto);
    }

    @GetMapping("/hotel/detail/{hotelId}")
    public String hotelDetail(@AuthenticationPrincipal CurrentUser currentUser,
                               Model model, @PathVariable("hotelId")String strHotelId){
        int hotelId = numberUtil.parseStrToInteger(strHotelId);
        if (hotelId != -1 && !hotelService.existsById(hotelId)) {
            return "redirect:/";
        }
        authenticationUtil.addUserDataInModel(currentUser,model);
        boolean isUserExist = currentUser != null;
        model.addAttribute("tourDetail",dataUtil.getModelDetailById(isUserExist ? currentUser.getUser().getId() : -1, hotelId,
                hotelService::getDetailById));
        return TemplateUtil.HOTEL_DETAIL;
    }


}