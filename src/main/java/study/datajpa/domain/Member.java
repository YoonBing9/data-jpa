package study.datajpa.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@ToString(of = {"id","userName","age"})
@NoArgsConstructor(access = PROTECTED)
@Getter
@Entity
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String userName;
    private int age;

    @JoinColumn(name = "team_id")
    @ManyToOne(fetch = LAZY, cascade = CascadeType.ALL)
    private Team team;

    private Member(String userName) {
        this(userName, 0);
    }

    private Member(String userName, int age) {
        this(userName, age, null);
    }

    private Member(String userName, int age, Team team) {
        this.userName = userName;
        this.age = age;
        if(team != null) {
            changeTeam(team);
        }
    }

    /**
     * 생성 메서드
     */
    public static Member createMember(String userName, int age, Team team) {
        return new Member(userName, age, team);
    }

    /**
     * 연관관계 편의 메서드
     */
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }

    /**
     * 비즈니스 메서드
     */
    public void changeAge(int age) {
        this.age = age;
    }
}
