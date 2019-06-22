package it.gym.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ExposesResourceFor;

import javax.persistence.*;
import java.util.Date;

@Entity
@DiscriminatorValue(value="H")
@JsonTypeName("H")
@ExposesResourceFor(value = Event.class)
@Data
@EqualsAndHashCode
public class TimeOff extends Event {

    private static final String TYPE = "T";

    @ManyToOne
    @JoinColumn(name = "user_id")
    //TODO change to Trainer
    private AUser user;

    public TimeOff() {

    }

    public TimeOff(String name, String type, AUser user, Date startTime, Date endTime) {
        this.user = user;
        this.setName(name);
        this.setStartTime(startTime);
        this.setEndTime(endTime);
    }

    public AUser getUser() {
        return user;
    }

    public void setUser(AUser user) {
        this.user = user;
    }

    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {

        return " Tipo :" + this.getType() +
                " Nome :" + this.getName() +
                " User :" + this.user.toString() +
                " Data :" + this.getStartTime().toString();
    }
}
