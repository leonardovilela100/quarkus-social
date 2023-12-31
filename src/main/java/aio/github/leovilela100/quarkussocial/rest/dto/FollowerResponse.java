package aio.github.leovilela100.quarkussocial.rest.dto;

import aio.github.leovilela100.quarkussocial.domain.model.Follower;
import lombok.Data;

@Data
public class FollowerResponse {
    private Long id;
    private String name;

    public FollowerResponse() {

    }

    public FollowerResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public FollowerResponse(Follower follower) {
       this(follower.getId(), follower.getFollower().getName());
    }

}
