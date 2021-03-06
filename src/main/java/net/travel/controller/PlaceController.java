package net.travel.controller;

import net.travel.config.security.CurrentUser;
import net.travel.dto.HotelDto;
import net.travel.dto.PlaceDto;
import net.travel.dto.SearchDto;
import net.travel.model.Hotel;
import net.travel.model.HotelImage;
import net.travel.model.Place;
import net.travel.model.PlaceImage;
import net.travel.service.PlaceService;
import net.travel.service.UserOrderService;
import net.travel.service.UserService;
import net.travel.service.WishListService;
import net.travel.util.AuthenticationUtil;
import net.travel.util.DataUtil;
import net.travel.util.NumberUtil;
import net.travel.util.TemplateUtil;
import net.travel.util.model.TourDetailData;
import net.travel.util.search.SearchModelType;
import net.travel.util.search.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @Autowired
    private AuthenticationUtil authenticationUtil;
    @Autowired
    private NumberUtil numberUtil;

    @Autowired
    private DataUtil dataUtil;

    @GetMapping("/place")
    public @ResponseBody
    ResponseEntity places(){
        List<PlaceDto> placeDtoList = placeService.getAllByDto();
        return ResponseEntity
                .ok(placeDtoList);
    }

    @GetMapping("/places")
    public String placesPage(@AuthenticationPrincipal CurrentUser currentUser, Model model){
        authenticationUtil.addUserDataInModel(currentUser,model);
        return TemplateUtil.PLACES;
    }

    @GetMapping("/places/search")
    public @ResponseBody
    ResponseEntity hotelsSearch(@RequestParam(value = "name",required = false,defaultValue = "-1")String name,
                                @RequestParam(value = "region",required = false,defaultValue = "-1")String region,
                                @RequestParam(value = "city",required = false,defaultValue = "-1")String city,
                                @RequestParam(value = "order",required = false,defaultValue = "new")String order,
                                Pageable pageable,
                                @AuthenticationPrincipal CurrentUser currentUser) {
        SearchParam searchParam = SearchParam
                .builder()
                .modelType(SearchModelType.PLACE)
                .name(name)
                .place("-1")
                .roomsCount("-1")
                .price("-1")
                .region(region)
                .city(city)
                .order(order)
                .userId(-1)
                .build();
        boolean isUserExist = currentUser != null;
        SearchDto<PlaceDto> searchDto = dataUtil.getSearchModel(searchParam,
                pageable,isUserExist ? currentUser.getUser().getId() : -1,placeService::getByParams);
        return ResponseEntity
                .ok(searchDto);
    }

    @GetMapping("/place/detail/{placeId}")
    public String placeDetail(@PathVariable("placeId")String placeIdStr,
                              @AuthenticationPrincipal CurrentUser currentUser,
                              Model model){
        int placeId = numberUtil.parseStrToInteger(placeIdStr);
        if (placeId == -1 || !placeService.existsById(placeId)) {
            return "redirect:/";
        }
        authenticationUtil.addUserDataInModel(currentUser,model);
        boolean isUserExist = currentUser != null;
        model.addAttribute("tourDetail",dataUtil.getModelDetailById(isUserExist ? currentUser.getUser().getId() : -1, placeId,
                placeService::getDetailById));
        return TemplateUtil.PLACE_DETAIL;
    }
}