package study.datajpa.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@ToString(of = {"id", "name"})
@NoArgsConstructor(access = PROTECTED)
@Getter
@Entity
public class Team {
    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    private Team(String name) {
        this.name = name;
    }

    /**
     * 생성 메서드
     */
    public static Team createTeam(String name) {
        return new Team(name);
    }
}
