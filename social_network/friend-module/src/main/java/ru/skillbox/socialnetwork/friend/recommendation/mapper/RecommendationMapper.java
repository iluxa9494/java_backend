package ru.skillbox.socialnetwork.friend.recommendation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.FriendDto;
import ru.skillbox.socialnetwork.friend.recommendation.dto.RecommendationFriendsDto;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {

    @Mapping(source = "friendId", target = "id")
    @Mapping(source = "friendId", target = "friendId")
    RecommendationFriendsDto friendDtoToRecommendationFriendsDto(FriendDto friendDto);

    List<RecommendationFriendsDto> friendDtoListToRecommendationFriendsDtoList(List<FriendDto> friendDtoList);

    default Page<RecommendationFriendsDto> friendDtoPageToRecommendationFriendsDtoPage(Page<FriendDto> friendDtoPage) {
        List<FriendDto> friendDtoList = friendDtoPage.getContent();
        List<RecommendationFriendsDto> recommendationFriendsDtoList = friendDtoListToRecommendationFriendsDtoList(friendDtoList);

        return new PageImpl<>(recommendationFriendsDtoList, friendDtoPage.getPageable(), friendDtoPage.getTotalElements());
    }


}
