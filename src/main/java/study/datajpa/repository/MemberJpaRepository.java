package study.datajpa.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.domain.Member;
import study.datajpa.repository.dto.MemberSearchCondition;
import study.datajpa.repository.dto.MemberTeamDto;
import study.datajpa.repository.dto.QMemberTeamDto;

import javax.persistence.EntityManager;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static study.datajpa.domain.QMember.member;
import static study.datajpa.domain.QTeam.team;

@RequiredArgsConstructor
@Transactional
@Repository
public class MemberJpaRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findById(Long memberId) {
        return em.find(Member.class, memberId);
    }

    public List<MemberTeamDto> findAll() {
        return queryFactory
                .select(new QMemberTeamDto(
                    member.id.as("memberId"),
                    member.userName,
                    member.age,
                    team.id.as("teamId"),
                    team.name.as("teamName")))
                .from(member)
                .leftJoin(member.team, team)
                .fetch();
    }

    public List<MemberTeamDto> findByName(String name) {
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.userName,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")))
                .from(member)
                .leftJoin(member.team, team)
                .where(member.userName.eq(name))
                .fetch();
    }

    public List<MemberTeamDto> search(MemberSearchCondition cond) {
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.userName,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                        )
                )
                .from(member)
                .leftJoin(member.team, team)
                .where(userNameEq(cond.getUsername()),
                        teamNameEq(cond.getTeamName()),
                        ageBetween(cond.getAgeGoe(), cond.getAgeLoe()))
                .fetch();
    }

    private BooleanExpression ageBetween(Integer ageGoe, Integer ageLoe) {
        if(ageGoe == null || ageLoe == null) {
            return null;
        }

        return ageGoe(ageGoe).and(ageLoe(ageLoe));
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null ? member.age.loe(ageLoe) : null;
    }

    private BooleanExpression teamNameEq(String teamName) {
        return hasText(teamName) ? team.name.eq(teamName) : null;
    }

    private BooleanExpression userNameEq(String username) {
        return hasText(username) ? member.userName.eq(username) : null;
    }


}
