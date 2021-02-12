package study.datajpa.repository.query;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import study.datajpa.domain.Member;
import study.datajpa.repository.dto.MemberSearchCondition;
import study.datajpa.repository.dto.MemberTeamDto;
import study.datajpa.repository.dto.QMemberTeamDto;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static study.datajpa.domain.QMember.member;
import static study.datajpa.domain.QTeam.team;

@RequiredArgsConstructor
@Repository
public class MemberQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Page<MemberTeamDto> searchWithPageSimple(MemberSearchCondition cond, Pageable pageable) {
        QueryResults<MemberTeamDto> memberTeamDtoQueryResults = queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.userName,
                        member.age,
                        team.id.as("teamId"),
                        team.name))
                .from(member)
                .leftJoin(member.team, team)
                .where(ageBetween(cond.getAgeGoe(), cond.getAgeLoe()),
                        userNameEq(cond.getUsername()),
                        teamNameEq(cond.getTeamName()))
                .orderBy(member.userName.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(memberTeamDtoQueryResults.getResults(), pageable, memberTeamDtoQueryResults.getTotal());
    }

    public Page<MemberTeamDto> searchWithPageComplex(MemberSearchCondition cond, Pageable pageable) {
        List<MemberTeamDto> results = queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.userName,
                        member.age,
                        team.id.as("teamId"),
                        team.name))
                .from(member)
                .leftJoin(member.team, team)
                .where(ageBetween(cond.getAgeGoe(), cond.getAgeLoe()),
                        userNameEq(cond.getUsername()),
                        teamNameEq(cond.getTeamName()))
                .orderBy(member.userName.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Member> countQuery = queryFactory
                .select(member)
                .from(member)
                .leftJoin(member.team, team)
                .where(ageBetween(cond.getAgeGoe(), cond.getAgeLoe()),
                        userNameEq(cond.getUsername()),
                        teamNameEq(cond.getTeamName()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchCount);
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
