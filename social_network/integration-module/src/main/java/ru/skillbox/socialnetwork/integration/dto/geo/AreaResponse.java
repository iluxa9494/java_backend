package ru.skillbox.socialnetwork.integration.dto.geo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AreaResponse {

    private Integer id;

    @JsonProperty("parent_id")
    private Integer parentId;

    private String name;

    @Builder.Default
    private List<AreaResponse> areas = new ArrayList<>();

    @JsonProperty("utc.offset")
    private ZoneOffset utcOffset;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        AreaResponse that = (AreaResponse) object;
        return Objects.equals(id, that.id) && Objects.equals(parentId, that.parentId) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parentId, name);
    }
}
