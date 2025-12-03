package ru.skillbox.socialnetwork.friend.friend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.skillbox.socialnetwork.friend.friend.dto.account.AccountDto;
import ru.skillbox.socialnetwork.friend.friend.dto.account.AccountSearchDto;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.FriendDto;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.FriendsSearchRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FriendMapper {


    @Mapping(source = "id", target = "friendId")
    @Mapping(source = "photoName", target = "photo")
    FriendDto accountDtoToFriendDtoWithoutStatus(AccountDto accountDto);

    List<FriendDto> accountDtoListToFriendDtoList(List<AccountDto> accountDtoList);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    public abstract AccountSearchDto toAccountSearchDto(FriendsSearchRequest request);

    default Page<FriendDto> accountDtoPageToFriendDtoPageWithoutStatus(Page<AccountDto> accountDtoPage) {
        List<AccountDto> accountDtoList = accountDtoPage.getContent();
        List<FriendDto> friendDtoList = accountDtoListToFriendDtoList(accountDtoList);

        return new PageImpl<>(friendDtoList, accountDtoPage.getPageable(), accountDtoPage.getTotalElements());
    }

}
