package ru.skillbox.socialnetwork.friend.friend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.FriendshipPairDto;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.UserRelationDto;
import ru.skillbox.socialnetwork.friend.friend.model.FriendshipPair;
import ru.skillbox.socialnetwork.friend.friend.model.UserRelation;
import ru.skillbox.socialnetwork.friend.friend.model.UserRelationId;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRelationMapper {

    @Mapping(target = "userId", source = "id.userId")
    @Mapping(target = "friendId", source = "id.friendId")
    UserRelationDto entityToDto(UserRelation userRelation);

    List<UserRelationDto> entityListToDtoList(List<UserRelation> userRelations);

    FriendshipPair dtoPairToEntityPair(FriendshipPairDto dtoPair);

    default UserRelation dtoToEntity(UserRelationDto userRelationDto) {

        if (userRelationDto == null) {
            return null;
        }

        UserRelation userRelation = new UserRelation();

        UserRelationId id = new UserRelationId();
        id.setUserId(userRelationDto.getUserId());
        id.setFriendId(userRelationDto.getFriendId());

        userRelation.setId(id);
        userRelation.setStatusCode(userRelationDto.getStatusCode());

        return userRelation;
    }
}
