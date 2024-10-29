package com.Px4.ChatAPI.services.community;

import com.Px4.ChatAPI.controllers.jwt.JwtRequestFilter;
import com.Px4.ChatAPI.models.community.ThinkingModel;
import com.Px4.ChatAPI.models.community.ThinkingRepository;
import com.Px4.ChatAPI.models.friend.FriendModel;
import com.Px4.ChatAPI.models.friend.FriendRepository;
import com.Px4.ChatAPI.services.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ThinkingService {
    @Autowired
    JwtRequestFilter jwtRequestFilter;

    @Autowired
    ThinkingRepository thinkingRepository;

    @Autowired
    FriendRepository friendRepository;

    @Autowired
    FriendService friendService;



    public ThinkingService()
    {
    }

    public ThinkingModel getMyThinkin() throws Exception
    {
         String USER_ID = jwtRequestFilter.getIdfromJWT();
        Optional<ThinkingModel> thinking = thinkingRepository.findByAccountID(USER_ID);

        return thinking.orElse(null);
    }

    public List<ThinkingModel> getFriendThinkin() // type: public - type friend + isfriend - only id == userid
    {
        try{
            String USER_ID = jwtRequestFilter.getIdfromJWT();

            List<ThinkingModel> listThinking= new ArrayList<>();
            List<FriendModel> listFriend = friendRepository.findAllByAccountID(USER_ID);
            listFriend.forEach(friend -> {
                String friendId = friend.getFriendID();

                Optional<ThinkingModel> thinking = thinkingRepository.findByAccountID(friendId);

                if(thinking.isPresent())
                {
                    String type = thinking.get().getType().toLowerCase();
                    List<String> shows = thinking.get().getShows();
                    boolean isFriend = friendService.isFriend(friendId);
                    boolean isBlocked = friendService.isBlocked(friendId);
                    if(type.equals("public") || (type.equals("friends") && isFriend && !isBlocked) || shows.contains(USER_ID))
                    {
                        listThinking.add(thinking.get());
                    }
                }
            });
            return listThinking;
        }
        catch (Exception e)
        {
            System.out.println( "ThinkingService-getFriendThinkin: " + e.getMessage());
        }

        return null;
    }

    public boolean createThinking(String title, String type, List<String> shows) throws Exception
    {
        switch (type.toLowerCase())
        {
            case "public":

                break;

            case "friends":

                break;

            case "only":
                if(shows == null || shows.isEmpty()) throw new Exception("community-Error when create thinking post");

                break;
        }
        return true;
    }

    public boolean deleteThinking()
    {
        return true;
    }


}
