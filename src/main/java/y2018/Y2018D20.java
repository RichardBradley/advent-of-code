package y2018;

import com.google.common.base.Stopwatch;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;

import java.awt.*;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.truth.Truth.assertThat;

public class Y2018D20 {
    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        // 1
        assertThat(getFurthestRoomDoorCount(example1.routeRegex, new HashMap<Point, Integer>())).isEqualTo(example1.furthestRoomDoorCount);
        assertThat(getFurthestRoomDoorCount(example2.routeRegex, new HashMap<Point, Integer>())).isEqualTo(example2.furthestRoomDoorCount);

        HashMap<Point, Integer> distsByLoc = new HashMap<>();
        System.out.println(getFurthestRoomDoorCount(inputRegex, distsByLoc));

        // 2
        // How many rooms have a shortest path from your current location
        // that pass through at least 1000 doors?
        System.out.println(distsByLoc.values().stream().filter(x -> x >= 1000).count());

        System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    private static int getFurthestRoomDoorCount(String routeRegex, HashMap<Point, Integer> distsByLoc) throws Exception {
        RegexAstNode regexAst = parseRegex(routeRegex);
        return regexAst.getFurthestRoomDoorCount(
                new RouteState(new Point(0, 0), 0),
                distsByLoc);
    }

    private static RegexAstNode parseRegex(String routeRegex) throws Exception {
        checkArgument(routeRegex.startsWith("^"));
        checkArgument(routeRegex.endsWith("$"));
        PushbackReader pr = new PushbackReader(new StringReader(routeRegex.substring(1)));
        RegexAstNode regex = parseRegex(pr);
        checkState(pr.read() == '$');
        return regex;
    }

    private static RegexAstNode parseRegex(PushbackReader input) throws IOException {
        List<RegexAstNode> acc = new ArrayList<>();

        while (true) {
            char next = (char) input.read();

            switch (next) {
                case '$':
                case '|':
                case ')':
                    input.unread(next);
                    return new RegexSeq(acc);
                case 'N':
                case 'E':
                case 'S':
                case 'W':
                    StringBuilder lit = new StringBuilder();
                    do {
                        lit.append(next);
                        next = (char) input.read();
                    } while ("NESW".indexOf(next) >= 0);
                    input.unread(next);
                    acc.add(new RegexLiteral(lit.toString()));
                    break;
                case '(':
                    List<RegexAstNode> children = new ArrayList<>();
                    do {
                        children.add(parseRegex(input));
                        next = (char) input.read();
                    } while (next == '|');
                    checkState(next == ')');
                    acc.add(new RegexChoice(children));
                    break;
                default:
                    throw new IllegalArgumentException("" + next);
            }
        }
    }

    @AllArgsConstructor
    static class RouteState implements Cloneable {
        Point location;
        int currentDoorsFromStart;

        @SneakyThrows
        public RouteState clone() {
            return (RouteState) super.clone();
        }
    }

    static interface RegexAstNode {

        int getFurthestRoomDoorCount(
                RouteState routeState,
                HashMap<Point, Integer> distsByLoc) throws RouteIsSlowerException;
    }

    @Value
    static class RegexSeq implements RegexAstNode {
        List<RegexAstNode> children;

        @Override
        public int getFurthestRoomDoorCount(RouteState routeState, HashMap<Point, Integer> distsByLoc) throws RouteIsSlowerException {
            int maxDist = routeState.currentDoorsFromStart;
            for (RegexAstNode child : children) {
                maxDist = Math.max(maxDist, child.getFurthestRoomDoorCount(routeState, distsByLoc));
            }
            return maxDist;
        }
    }

    @Value
    static class RegexLiteral implements RegexAstNode {
        String seq;

        @Override
        public int getFurthestRoomDoorCount(
                RouteState routeState,
                HashMap<Point, Integer> distsByLoc) throws RouteIsSlowerException {
            for (int i = 0; i < seq.length(); i++) {
                routeState.currentDoorsFromStart++;
                switch (seq.charAt(i)) {
                    case 'N':
                        routeState.location = new Point(routeState.location.x, routeState.location.y - 1);
                        break;
                    case 'E':
                        routeState.location = new Point(routeState.location.x - 1, routeState.location.y);
                        break;
                    case 'S':
                        routeState.location = new Point(routeState.location.x, routeState.location.y + 1);
                        break;
                    case 'W':
                        routeState.location = new Point(routeState.location.x + 1, routeState.location.y);
                        break;
                    default:
                        throw new IllegalArgumentException("" + seq.charAt(i));
                }

                Integer prevDist = distsByLoc.put(routeState.location, routeState.currentDoorsFromStart);
                if (prevDist != null) {
                    // room reached in two different ways:
                    if (prevDist < routeState.currentDoorsFromStart) {
                        throw new RouteIsSlowerException();
                    }
                }
            }
            return routeState.currentDoorsFromStart;
        }
    }

    @Value
    static class RegexChoice implements RegexAstNode {
        List<RegexAstNode> children;

        @Override
        public int getFurthestRoomDoorCount(RouteState routeState, HashMap<Point, Integer> distsByLoc) {
            int maxDist = routeState.currentDoorsFromStart;
            for (RegexAstNode child : children) {
                try {
                    maxDist = Math.max(
                            maxDist,
                            child.getFurthestRoomDoorCount(
                                    routeState.clone(),
                                    distsByLoc));
                } catch (RouteIsSlowerException e) {
                    // continue;
                }
            }
            return maxDist;
        }
    }

    static class RouteIsSlowerException extends Exception {
    }

    @AllArgsConstructor
    static class Example {
        String routeRegex;
        int furthestRoomDoorCount;
        String drawnMap;
    }

    static Example exampleX = new Example(
            "^ENWWW(NEEE|SSE(EE|N))$",
            -1,
            "#########\n" +
                    "#.|.|.|.#\n" +
                    "#-#######\n" +
                    "#.|.|.|.#\n" +
                    "#-#####-#\n" +
                    "#.#.#X|.#\n" +
                    "#-#-#####\n" +
                    "#.|.|.|.#\n" +
                    "#########");

    static Example example1 = new Example(
            "^ESSWWN(E|NNENN(EESS(WNSE|)SSS|WWWSSSSE(SW|NNNE)))$",
            23,
            "#############\n" +
                    "#.|.|.|.|.|.#\n" +
                    "#-#####-###-#\n" +
                    "#.#.|.#.#.#.#\n" +
                    "#-#-###-#-#-#\n" +
                    "#.#.#.|.#.|.#\n" +
                    "#-#-#-#####-#\n" +
                    "#.#.#.#X|.#.#\n" +
                    "#-#-#-###-#-#\n" +
                    "#.|.#.|.#.#.#\n" +
                    "###-#-###-#-#\n" +
                    "#.|.#.|.|.#.#\n" +
                    "#############");

    static Example example2 = new Example(
            "^WSSEESWWWNW(S|NENNEEEENN(ESSSSW(NWSW|SSEN)|WSWWN(E|WWS(E|SS))))$",
            31,
            "###############\n" +
                    "#.|.|.|.#.|.|.#\n" +
                    "#-###-###-#-#-#\n" +
                    "#.|.#.|.|.#.#.#\n" +
                    "#-#########-#-#\n" +
                    "#.#.|.|.|.|.#.#\n" +
                    "#-#-#########-#\n" +
                    "#.#.#.|X#.|.#.#\n" +
                    "###-#-###-#-#-#\n" +
                    "#.|.#.#.|.#.|.#\n" +
                    "#-###-#####-###\n" +
                    "#.|.#.|.|.#.#.#\n" +
                    "#-#-#####-#-#-#\n" +
                    "#.#.|.|.|.#.|.#\n" +
                    "###############");

    static String inputRegex = "^SEESSSWSSEESWWWSWWSEEESSSSSSEEEEENESESWWWSEEESSSENEEESWSESWSSWSEESEEENEENEEEENEENENNWWS(E|SWNNNENNWNWWSSSSSE(SSWWWWNWSS(E|WW(SEWN|)WNENNNNEEESWS(EENNE(SSS(W|E)|NWWWNEEENNWSWWWSWWS(WWNNW(SS|NNNESES(S(EENWNNEES(SEENEEENWNENWWWSESWW(NNWNWNWNNENENWNNENWNEEESEENENWNNWNWSSES(WWNWWNWWNWNENWWWNENWNWWWSWWNNE(S|NWWSSSSSSWWSWWWWSWWSEESWSSWSEEENN(WSNE|)ENWNEE(EN(ENEESWSES(WWNSEE|)SEENEEESSWSSEESEES(ENNWWNEENNN(NWSWNNWWS(ESSS(EENW|SW)|WWNWW(NNENN(ENESSSS(ENESNWSW|)(S|W(W|NN))|WWS(S|E))|S(E|S)))|EE)|WWSSSSESS(WNWNWNNWNWNEEE(NWWNNWSSW(NNN(EENE|WSSSW)|SSSSSSSSS(ENNNNE(NWNSES|)SSSEN(ESSNNW|)N|WWNENWNENWNN(ESNW|)W))|S(SS|W))|E(S|EN(ESEWNW|)NW(S|NWNENW))))|WWW)|SSSSE(NESNWS|)SWW(NN|SEESWSWNWSWWNNE(ENWWWSWNWWSSE(ESE(N|SSSSWSEEENWNENE(NWWSNEES|)SEESSWSEE(NNN|SSSSSESENESSSWSSWNNN(WWWSWNNWWWWSSWNWSSSWSEENNEESWSESESEENWNWNNE(ES(ES(W|ESSW(SEENENWN(EEESSW(N|SEEEENENWNNNNE(NNWSWW(NEN(EE|WNWWN(WSNE|)EE)|SESSW(WNEWSE|)SESSWN)|EEE(SSWSW(SESSEE(NWES|)SESWWWWN(W(NENSWS|)SWSSSWSEESEESENEEEENNWWWS(EE|WNWSWNWNNESENESENESEENEESWSSENENNNESENENESSWSWWSSENEENESESENESESSSWWNN(ESNW|)WSWWSSEE(NWES|)SESWSWWNENWWNNWNWWWSESSWSEEE(NNW(N|S)|SE(SWWWWWSWNWSWNWSWWWWNWNWSSESEESSWWN(E|WNWWNNWWNWWNWSSWWSWSWNWSSEEESSSEEEENENWNWSSWWNENWN(W|NESENE(NWES|)SESE(NNWESS|)ESSES(WW(W|NN)|EN(NWNSES|)EES(EEN(NNNESENESSWSES(WWNNSSEE|)ENESEENNEESS(WNSE|)ENNNESSES(W|EENEENWWW(S|WNNWWWSWWN(EN(EEEENESSENESEES(WWWW|ESSW(S(WW|EENNENWNENENESESSWW(NEWS|)SSS(WNSE|)EEEENENESS(ENNENEENWNNESENNEEEESENNWWWWNENNNWNEEESWSSSENNENESENENNWSWWNENWNEEES(W|E(NNWWNNEE(SWEN|)NWNENWWNNNNESE(SWSEWNEN|)NNNNWWSESWWSWNWWSSE(EESSSWNNWWSESWSSSWWWNNWWNWWWSESE(ESSSESENESSEES(WWWWN(WSWSEESSSSWNWSWSSWWSWNNNNNWSSWSESS(WNWNWSWSWSW(S|NNNWNEN(ESE(SWSNEN|)NENEE(SWSWENEN|)NNENNWSWNNWWSESSS(ENSW|)WNNWW(SESWSEE(WWNENWESWSEE|)|NWNWWNW(WNNNESES(W|ESEEE(S(S|W)|NEEENWWNWWNEEEEENEENNES(ENEES(W|ENNWNWWWS(WWWSESWWWS(E|WWNNNNESS(ENE(NENESENENENWWSWNWWS(E|SW(NWSWWSSWSSE(N|SWWNW(NENNENNENE(S|EENEEENWWNWWNNWSSWNNWSWSWNNWSSW(SEEESWSE(ENN(N|ESENE(S|E))|S(WWNNSSEE|)S)|WNW(SWEN|)NNNNENNNWSWNWWSESWWW(WN(W|EN(E(S|NENESEENNW(S|WWN(WW(S(ESWSNENW|)W|N)|EENENNNNW(S|NNNESSEEESESWSWSEENESSSSWWNENWWW(S(W|SSSESSESSSW(WSSE(NEENENWNNNWNEEN(WWWNNSSEEE|)ESSESWS(WNNSSE|)EEEENWNEEENNNWNWNEESESENNESENESSEENNW(S|NWWNWWWS(EE|S|WNNWNWNNNEESWSESESENESEEENNESSESEESWSSSSEENWNNEEEEESSWWN(W(SSESESESSEEE(NNWNENNWW(S(E|S(SSE|WNW))|NEENNNNWSWNWWSS(E(ESENSWNW|)N|WNNNENEENWWNENNNNWWWSESE(SWSSSWNWNNE(NWNWNENNESEENNESEENE(SSWSESWW(NNWESS|)SSSEE(NNWSNESS|)SWSS(ENSW|)WW|NWNWNENWNWWNWWSESEESWWSWWWWNNEE(NWWNENWNWNWWSESESSWSSE(N|SSSENES(EEN(E(SS|ENESES)|W)|SWWWSWNWWSSWNNWNNNESEES(WW|ENN(ESSNNW|)NNWSW(SEWN|)WNWSWSWNNNNWNENESSENEEE(NWNENENNWWWNWSSWNWWNNWSSWSSSSSE(NNE(EEN(EE(SSWNSENN|)EN(W|E)|WWN(WSNE|)E)|S)|SSWNWWSWSWWSEESSEENWNNE(N|ES(W|SESE(NNWNEN|SEE(NWES|)SSESWSSS(WNNWSWWWNWNWNNWSWSWNNNE(E(EEEE(ESWSEENESSWWW(WNNWS|SE)|N)|NNWSWWWNEENNNNWNWWSWNNWNEESEEEEENNWNNWSWSES(E|WWNNNNNWSWNNENNEESS(WNSE|)ENENNNWNNWSWSS(WNNNENENWWSWSWNNENEEENNWWS(WNNENNEENWWNNENWWWWSSENESSWWSEESWWWSSWWSWNWSSSSSWSESSWWN(NWNWWNNNENESESWWSEE(S|ENNNWNENNWNNWNNNWSSWSESSSS(WNNNWSWSWNWNEENNE(NNNWNENWNEESEESWWS(EEEESWSSEESENNNW(SWEN|)NEEESWSSEE(N(W|NNEENEEEEENESSESSSWNWS(SSSS(WWNEWSEE|)ESWSSESS(WNSE|)ENESENESESEENEESWSSSSENNEESWSSEEESEESS(WWN(E|W(WWWN(EEE|NWS(SSEWNN|)WWWNWWNWW(NENWNEN(WWSNEE|)EESSS(WNNSSE|)EENWNEESSSW(SEWN|)WW|SSWSES(WWNWSS|SSESEE(NNNWNE(ESNW|)NWW(SSSESNWNNN|)N|SWWSS(EENWESWW|)WW(SE(SWEN|)E|W)))))|S))|ENNESSSSWW(NEWS|)SEEEESW(SEES(WSNE|)EEENNNNWSWW(SEESWENWWN|)NENWWSWNNNENENWNNESESSSES(WW(N|W)|SEESENNE(SSSWWSSSS(W|ENE(NWNEWSES|)SS(W|SSS))|NNWWNWS(SSENEWSWNN|)WNNNWNEESSENENWNNNWSSWWNNWSSWS(E|WNWNWWNNWSWSWWNNE(ENWWNEENESEENWNEENESSENEESWSSS(EE(EENWNENESSSSE(SS(WNSE|)SSSSWN|NNNNNNWWWNNWSWNWWS(WNWNWSWNWWSWNNWSWSSWSESWSWNNWWWWNEEEENENWNEENNEES(W|ENEENWWWNNWWWSS(EENWESWW|)WWNENWWWNWSWWW(WNWNN(WSWSWNN(WSSSES(WWWSWNWNNWSWWSEES(WWWNW(NENWWSWNWNEN(ESEEN(W|ESE(SWEN|)NEES(W|ENESSWSEENNN(SSSWWNSEENNN|)))|WWWWWSWSWWWWWNEN(WWWWWWSWWWSESSWNWWWSSWSWNNWNENE(NWN(WWSWSSE(NENSWS|)SWSWWSWWSESWSSESEEESEEESEESESEENESEENNEENESSWSW(SSWWN(E|WWSSWSSSWNWNNNE(ENWWWN(E|WWWNN(ESEEWWNW|)WSSWNNWN(E|WWNWNNWWNNNESE(SWEN|)NNNNENNEEE(SSSWNW(NE|SWSE)|N(E|WWWWWSS(ENSW|)SSWS(WWSSE(N|SSWNWNNWWWNEENNWNWNEEN(WWWWWSSWWWNWN(EEESWENWWW|)WWSSSEN(N|ESSWWWSSSWWNWSSEESWSWNWWSESSSEEENWN(WSNE|)ENENEN(W|ESENNWWNEN(W|ESEENENEN(ESSE(NE(NWNNWS|E)|SSSSENNEESSSW(WSEESSSWWNENWWSSWNNWNNE(ESWENW|)NWWNENE(NW(WSWSSSW(SSSSWNNNWN(WSSSSW(SESEN(NNN|ESEESWSEENESENENEN(ESSESSWWN(WSWSSSWSESESEENWNNW(S|NEEN(WW|ESEENWNENWNNWNEESES(ENNNNNNN(WSSWNW(N(E|N)|SSES(W|E(S|N)))|ESSESSENESSE(SSEESSSWWSWSSSWSWWSEESEEENENNWSW(WSEWNE|)NNNENEESWS(ESSESWSEEESWSEENENNW(WNEEESEESSWW(NEWS|)SSSWWWWWSWWSESSSWWSWSESENEEESEEEEENEESWSSWSESEENWNNEES(W|SSEEESSSSWNNWN(E|WSSWNWN(E|WWSESWSSENE(N|ESSE(SWSWWWWWSSSESSWSWNWNWNWWWSESE(N|SWWSESENEN(N|ESESWWSESWWSWNWWWNNE(SE(N|EE)|NWNWNWSSSE(SWSESEESSWSESWSWSESESWWSWNN(NWWNENWWWSWWWSESESEESWWWNWNWNNNNNNWNWNEENWWWWWNENEENENESSWS(EESSS(W|SESWSEE(SWWEEN|)NESENESENNE(NWNNWNENWNWNNWWSESSWW(SESWSSENEE(NWNENSWSES|)ESSWW(NEWS|)W|WNE(E|NWNWNNENWNNENNESSS(W|SSS(S|W|EEENESSE(NNNNENWNENNWNENWNNNENNWWWWNNWSWWWNEENWWWNNWSWNWNNEEENEEENWWWWNNWWWNENNWWWNNNEE(NWNNWN(EESSENN(SSWNNWESSENN|)|WNNWSSWNWSSESSSSEE(SWSSE(EE(E|SWSESWSSENENESS(WSSWWN(WNWSWNWNWNEESEENWNENWWNNWSSWWWWNNNWNWNNW(NEESENNENESE(SWWSESSSW(NNW(W|S)|SSENE(S|NE(E|NWNNE(N|S))))|NNWWWSWNNENEEE(S(E(E|N)|WW)|NWWWN(WSSWSSW(NNNENNENWN(W(NENWNNNEENWNENENESSES(E(S|NNENWW(S|NWWWWS(W(NNEEEEEEESW(ENWWWWEEEESW|)|S(E|SS))|E)))|WW(SSWWSEE(WWNEENSWWSEE|)|N))|SSS)|E)|SSE(N|E))|EEE)))|SSSSSSEE(SSSSSENESSEEN(NW(NWNEN(WWSSNNEE|)EEE|S)|ESSESEESEEEN(NWSWNWNW(S|W)|ESSSWWWSWSESSSWWNENWNNWNWSWSWSWWSWSSEEEENN(NENESSSSW(NN|SEENESESSSENENENNEEENEENESESENNWNEESE(SSSW(SWSSESWSES(ENNNN(W|N|E)|WSWNNNNNNWN(EE|NWWSS(ENSW|)WWN(WSSESWSSEE(N(NENWESWS|)W|SSSWS(WWNWWNENE(NWWN(EEN(W|N)|WWNWSSWSEEE(NWES|)(E|SSWNWWWWWSESWSSWWNENWWWNEEENNNWWNENNWSWNNN(W(SSSSESSW(S(EEENWESWWW|)SSSSSEE(NWNEWSES|)SSSSSSEENWNNNENWNEESEESSSSWNW(SSESSEESENNESSSSWSEESSWSESWWWWSWWWSESENEENESSSEEEENWNWW(SEWN|)NEENNENENNENENNNE(SSSSWSSSSESWWSEESESWWSSSEENN(WSNE|)EEENNENESESWWSSESWSEENNN(W|ESENEEEESWWWSWSSEEENESSENEENEENNESSEESEENWNNWSWNNEEESSESSESSWSWWNEN(E|WWWWN(E|WSW(SESSENNESE(N|SWSEENEESENNESSESSEE(EENENESENNWNENWNNWSSSSWSWNNENNNWSWWWW(SEESWSEE(SWSEE|NNE)|NEENWWWNEEENE(ESS(EENN(WSNE|)E(N|EESSW(NWSNES|)SES(W|E(SWSSENESE(N|ESSEEN(WNSE|)EENESSEENWNEE(NEE(EEE(EN(ESNW|)W|S)|N)|SS(E|SWWWWNWSWWWWNNWSWNWSSWS(WWNEWSEE|)SENE(SSS|E(NWES|)EEEESE(SWWNWESEEN|)EN(W|EEEES(EEE|WWW))))))|NENNN(WSSNNE|)E)))|W(S|N))|NWWSWNWN(E|W(SSEWNN|)WN(EE|WSWNWWNWSWSS(EEN(E|W)|SSWWW(SE|WWWSW|NNENE(SSWENN|)NNWNNNWWNEEESSSE(NEENEES(SW(SEWN|)(N|WW)|ENNNNNNWSWSWWSS(W(SEWN|)NWWWW(NENWNEESEE(SWWEEN|)EENNE(S|EEESSSSENNNNNN(WNWSS(WWWWNEENE(S|NNNNNE(NNNNENENWWSW(SSSWS(E|SSW(NN|WSESS(ENNSSW|)WS(E|WWSWNWW(N(N|EEENE(E|S))|SESES(ESEN(ESNW|)N(N|W)|WW(WSNE|)N)))))|NWNEEEEE(ESWSSW|N))|ESWSEES(ENNWNSESSW|)S(WNWSNESE|)S))|E)|EESSENNEE(NNEWSS|)SSW(N|SESS(WWWWNN(ESE(E|N)|NN)|ESE(E|NNWNENWNE(WSESWSNENWNE|))))))|WWSWSSEE(N(W|N)|ESE(SWSE(E|SWWNNNWSSSWWWNNEN(ESSWENNW|)WW(WNNN(WWWSWNWS(WNNE(E|N)|SSES(W|SE(SWEN|)NENENW(NEWS|)W(S|W)))|EE(NWNN(ESNW|)WWN(ENSW|)W|ESWS(WNSE|)E))|S))|N)))|ENESENN(SSWNWSNESENN|)))|S)))))))|SSWWWN(NNWSWSESS(EE|WWWNNWNN(ESE(SSEWNN|)N(N|E)|WSSSSSE(NN|EESE(SWSEESWWWWNWSS(EEEEEEENNWS(NESSWWEENNWS|)|WWWWWWNNWNENENNNNNEESSW(N|SSSSSENENWNEE(NW(W|NNNNWWWNEE(NWWWSWWSWSSWNNWNENNEEN(NWSWNWSWNWSWSSSENNES(ENSW|)SSWSEESWSWNWSSSEESSWWSWSEEES(EEEEN(ESNW|)WWWN(ENWNEEE(SSWNSENN|)NNWSWWN(WW|NE(S|EENNENESSWSESWS(E|S)))|WW)|WWWWNWNNE(NWNNNNNWWNWSWNWSWNWNWSSWWSESWWNWSSSWNNWWNWSWWSEESSESESENN(WNNWSNESSE|)EENNESSSSSWW(NNESNWSS|)WS(WWWNN(ESEWNW|)WN(WNENWWW(NENNENWW(SS|NENNNNESEESSESS(ENEES(WSWENE|)ENNESENNEENENWWSWWWS(WSWWNENENNWSWNNNWNWNWSSESWS(WWW(NENWNNEE(S(W|SS)|NEEEN(EEE(NWES|)SSENESSWSS(SSEENNESEENWNENE(NNW(SWSWSW(WSSNNE|)NNNE(E|S)|NN(NWNNE(S|EN(NN|W))|E(E|S)))|SEESSEESESWSWS(WNNWW(SESWWN|NE(NNWS|EES))|EEES(E(ESWWSESWSSE(S(S|EE)|N)|NNW(W|NNE(S|NN(WNWS(SEWN|)WN(WSNE|)NNEESW|E(EENNSSWW|)S))))|WW)))|WNNW(SS|NNWS(S|W)))|WNWSWWNNW(SSSEWNNN|)NENNESSS(S|ENNENENEENWNENWNE(NWWNWSSSWWWSW(NNEEENNN(EE|WWSS(ENSW|)WNNNNNNNNESSSSS(NNNNNWESSSSS|))|SES(WSSNNE|)ENNESS(ENEN(W|NN)|S))|E(SSSE(SSW(WWSWENEE|)N|E)|E)))))|SSSS)|ES(E(NN|S(S|E))|W))|E)|WNW(WNNESNWSSE|)SS))|SESSESWS(EEN|WNNN))|E)|EEEENEES(EENWNEENNWWS(WWS(WNNNESENENNWW(NE(EEEEESWSS(WNNWSS|EE(SSSWSS(E(N|E)|WN(NNENW|WS))|N(NN|W)))|N)|S(W|E))|E)|E)|W))|S))|ES(SWWSNEEN|)ENESE(NNWESS|)E)|EESSSS))|SES(ENESSNNWSW|)WSWS(WWWNNSSEEE|)E)))|N))))|EE)))|N))))|NNWSWWNEN(E|NWN(E|WWNNNNNE(NWWWWWSSESSW(SSSSS(SWNSEN|)ENEEN(ESE(SWW(SSSSEENWNNESEESSW(N|SWW(SS(ENSW|)S|W))|W)|N|E)|WNENW(WSSNNE|)NENNW(N(E|W)|S))|NWNW(NENNEE|S))|S(S|ENE(E|S))))))|N(WS|ENW))|N)|NENNNENWW(SSS|NNNENNNW(SS|NNNNE(NWNEWSES|)SSSESE(N|SENESSWSES(WWW(NN(ESNW|)N|S(WNSE|)EE(SWEN|)E)|ENNE(NWNSES|)SENESSENEEE(WWWSWNSENEEE|))))))|EES(W|EEN(W|ESESE(NESENSWNWS|)SWS(SSWWNENNWN(WSSEWNNE|)E|E))))))|ES(ENSW|)(W|S))|E))|E)))|NN)|NN(E|WWWWSWWSWNNWWSESWWNWW(NEEN(EENE(SEE(SWEN|)EE|NWNWSS)|WW)|SES(EEE|WS(S(ENSW|)S|W))))))|WSW(W|N))))|NWN(E|N)))|E)|E(E|NN)))|N)|NNN(ESNW|)W(NEWS|)SS))|SS(WNSE|)E(ESEEEESWWWS(WNSE|)EESWSEENNESSENENESSWSESWW(SWWWS(SENEEESESEENWNENW(NNNESSENNNEN(WWSWNN(WNNNNWN(NENWNNESENESES(ENN(ESENSWNW|)WNWNN(ESESNWNW|)W(NN|SS)|SSWS(E|SWNN(NENWESWS|)W))|WSSSE(SWW(NNNW(WNEEWWSE|)SS|SE(E|S))|N))|E)|ESEEES(SWWSWNW(NEEEWWWS|)SSESS(EENWNSESWW|)SWSW(SES(WWNWSWNN(WNW(S|W(NEWS|)WW)|EE)|SSEN(EESSESESSWSWWWSSW(NNNEENE(NWWW(NEENSWWS|)S(WNSE|)E|S|E)|SEESSEEENWWNEEESSESWS(ESE(SEWN|)NNNNWNENNWWS(E|WWWWNEEENEEENNEE(N(NNWN(EE|WSWNWWNWSSESE(N|SSSENNNESEN(SWNWSSNNESEN|)))|E)|SSW(N|SESSS(WNNSSE|)EEEN(N|WW))))|WWWWSSW(SE(E|S)|NWNW(SSEWNN|)NEE(NWNEWSES|)(S|EEEE))))|N))|NNN(ESNW|)N)|E(EEEENSWWWW|)NNWW(W|N)))|W(W|S))|WWW)|N)|N))|S(W|S))))))|SSE(SWS|NNE)))|WW)|E)|N))))|EN(N(WSNE|)N|E(S|EEEENWWNEENNNNNWNNWWSESS(ESSSWNN(SSENNNSSSWNN|)|WNWSWNNNE(NNWSWSWNNWWWNWWWNWNNEEN(WWWSSW(N|SESES(WW(N|WW)|EEESWWW(EEENWWEESWWW|)))|EESSS(WN(N|W(W|S))|EEES(WW|E(S|ENEES(SSEENESSES(W|SE(NENENESSEEENENEEENNNEEENNWSWWNENENENNWNENNENNNWNNESESSESWSEE(SSESE(SWWNWNNWSW(N|SSSS(EE(NWN(E|N)|SESSSENESES(ENNWNSESSW|)WWSESSSENESSWSWWSSWS(EEESENESS(ENNENE(ESSW(WSS(ENEESE(NNWNEEE(WWWSESNWNEEE|)|SWSSSWW(NNN(WSNE|)E(SS|N)|SSSESEEE(ESWWW(SS|W)|NWWNWNEE(S|NNNESSEN(SWNNWSNESSEN|)))))|W)|N)|NW(N|WWS(WWWNEEN(SWWSEEWWNEEN|)|E)))|WW)|SWS(ESNW|)WWWS(ESWENW|)WNNNNESEE(SWWEEN|)NNNENWNENEN(ESSS(E|W(SSWSSNNENN|)N)|NNWNWSSWSE(ENSW|)SWWSESWSES(S|WWNWSWW(NENEENWWNWN(WSWSSWNWSW(SEEEE(NENWESWS|)S|NWW(W|N))|EEE(SWEN|)ENNEN(NNNESESW(ENWNWSNESESW|)|WWSSWNN(SSENNEWSSWNN|)))|SES(E(SSS|N)|W))))))|W))|N)|NNENWNENWW(SS|WNWWW(SSSWSES(ENSW|)WSESWWSESWWNNWSWWNWWSWSSSSSWSWSESENESEES(SWW(SEWN|)N(E|WWWSW(S|NW(S|NE(NNWNNESENENNWWNWNNNNWNNNESENNNNESSSSSENENESSEEENE(NNWNWSS(E|W(SEWN|)NNWSWW(NENWNWWWWNENENNNE(NWWWWNWW(WWNWWNEEENWWWNNWN(NEENENEN(ESSWSEESSWNWSWN(N|W(W|SSEEESENESSEESW(SEEENWNNNESES(W|E(SWEN|)NENWNNNNN(NEESWENWWS|)WSWWSWNWW(SESWSEE(SWSEWNEN|)EN(W|ESE(NNWESS|)S)|W))|WWW(NEWS|)W)))|WWSWWSE(WNEENEWSWWSE|))|WWSWSWSSWSSW(SEEENWNEESSENE(NWNNE(NW(WSSWNSENNE|)N|S)|EESWWS(WWW(W|S)|S))|WNENWWNEN(EE(SWEN|)NENNES|WNNWSSWSESS(SENSWN|)WWWN(NNE(SSE|NE)|W))))|SSE(SWSSSSESWSWWWNWN(WW(SWSEE(N|ESESSSWWNWSW(SEESSENNESENNESESSEEE(SWSSE(N|S(SWSSSE(NN|SWW(S|NNWWNWNEESENN(NWNWN(EESNWW|)N(WWSWWSWSEEEN(W|E(SS(E(N|E)|WWSESWWW(SEEE(S|E)|NN(WNN|ES)))|N))|N)|E))|E)|E(N|EE)))|NWNNNNNW(WWSW(SEESE(NNWESS|)SS|N)|NEN(NNWNEENW(W|NNESE(S|N))|EESWSE)))|WNWN(EE(S|EN(ESESNWNW|)W)|WSS(WNNN(NWSW(SESS(WNSE|)SS|NNW(S|NNEE(NWWWWEEEES|)S(W|E(SWEN|)N)))|E)|E))))|N)|EEE(SWEN|)NN(WSNE|)N)|N))|ESWSSEEN(ESE(SSSWNNW(S|WWW)|N(N(N|WW)|E))|W))|S))|EESSSWWW(S|WN(EENESNWSWW|)WWS(WWN(ENSW|)WW(NWES|)SESS(WN|SENNE)|E)))|E))))|EENE(S|NNNNWSSSW(S|NWW(SEWN|)NW(SWEN|)NNEES(W|E(SWEN|)NNWN(W(SWNSEN|)N|EESES(E(S|NE(S|E))|W))))))|NNNNEEEE(SWSS(E(SEEWWN|)N|WWNNES)|N(E(NWNEWSES|)S|WW)))))|SSSSSSWNNNNN(SSSSSEWNNNNN|)))|W)))))|S)))))))))|S)|W)|N))|W)))|N|E)|WWSWNNNWW(NEN(N|W)|SS(W|E(S|N)))))|NNN)|E)|NNN)|N)|S)|NN))|WWSW(S|N)))))|EEEESWSWNWS(W|SS(S|ENE(ENSW|)S))))|E)))))|SS))|N)|EESSEENWNEEEE(WWWWSEWNEEEE|))|SS)|EES(ENEWSW|)W))|S)|E)|ENE(S|E|N))|E)|ESESEENWN(EESEEEN(ESENEESS(EENN(WSNE|)EESEEESSWSESWSW(SES(ENEENEENWWW(SWEN|)NNNNN(WWW|EEESWWSSSENNESSENENWNEN(W|ESE(N|SWSESSWW(NEWS|)WSEEESSSWSESWWNNNWN(EESNWW|)WWSES(E|WWNWSW))))|WWNWSW)|NNWWN(WSNE|)N(N|EE(SWEN|)E))|W(N|WWWW))|WW)|W))|SSEEEEE(NWWWWEEEES|)SWSWSSENESS(WWWNW(NENWNEE(WWSESWENWNEE|)|SWSS(WNNSSE|)SSS(ENNNE(NWES|)SSENEESWSWS(W(N|WW)|ESE(ENN(WSNE|)(NN|EESE(S(WWNSEE|)SSS(EEESWSS(W|ENENEN(WNSE|)ESSE(SWW(N|S(WNSE|)EEE(S|NN))|N))|W)|NNENNN(N|ESS(S|EENN(ESENE|WS|N)))))|S))|W))|ENNN(NEESWENWWS|)W)))|SE(N|EESS(ENEE|SSWN))))|SS)|WWWW(SEESEN|N(WW|EN(EESWENWW|)N)))|S))))|W))|W(W|NNE(NWWWWEEEES|)E)))|SWS(W(WSS(ENES|WNNWN)|N)|E))|SS)|S)|EN(E|NN)))|E)|E)|E(ESNW|)N)))|S)|ENESEEENENESE(NNN(E|WSWWW(S(W(SEWN|)WW|E)|N))|SWSESW(SEE(ESESSW(SEWN|)NWWN(E|W)|NNNN)|WWN(ENSW|)WSSW(SEWN|)NWN(E|WSW(SEEWWN|)N))))))))|SWS(W(WWSNEE|)N|EEN(E|N)|S)))))|S(ENSW|)W))|S)|N)))|SSSWWWNEENWWWWWNNWSWSWWSEEE(SEEE(NWWEES|)SSWSEESEENNE(SSSWWWSWWS(WNNN(ESENSWNW|)NWNWSWNWNN(EES(EE(S|E)|W)|WNWNNNWWNWSWNW(SWWSSSSW(NNWWSWNWSS(EEEN|WS)|SES(W|ENNENESSENNENN(WSWNWSW(S|N)|ESSSWSEE(N|ESWWSWWW(SSENESENEESEEN(WNWWEESE|)EESSSSSSSWSESSSWNNWWNENWNNNNN(EE(SWSSSENN(SSWNNNSSSENN|)|N)|WW(WSS(ENESSNNWSW|)W|N))|N(W(SWEN|)N|EE))))))|NEN(W|EE(NWES|)S(EEN(EE(SEES(EE(ESSEN|NW)|WS(SWW(S|NN(ESNW|)(N|WW))|E))|NNNNWNWW(EESESSNNWNWW|))|W)|W))))|EE)|NWWWSES(NWNEEEWWWSES|))|N))|W)|E)))|S)|NN))|NNNENW)))))|W))|SEEEENE(WSWWWWEEEENE|))))|WWSEES(WWWSWWSWNN(WWSESWSWWW(NEN(ESNW|)WW|S)|NEE(SWEN|)E)|ES(W|SE(NN|E(SWEN|)E)))))|S))|S)|S))|EE))|SSSESSWWSEEEE(NWNEWSES|)S(WWSSE(NEWS|)SWWNW(SS(W|EESE(NEESWENWWS|)SWW(SSENSWNN|)N)|NN(ESNW|)WNNNN(W(WW|SSS(WWW|S))|E(SE|NW)))|E))))|S))|WW(W|SSSESSWNW(N|W))))|SEEENEE(SWS(SWNWWS(SW(SWWNEWSEEN|)NN|E)|E)|NNESENENNENWNN(SSESWSNENWNN|)))|E)|ENNWWNN(WWWNNSSEEE|)EEEE(SWWWEEEN|)NNENWWWSESW(ENWNEEWWSESW|))|N)|N)|SSSSSW(N|SWW(SSSSESSWWN(E|NWSWSWSWW(NNE(S|NE(N(ESNW|)W|S))|SEES(ENEN(N|W|ESENEENNNNWNNESE(SSSSSSSWNWSWWW(EEENESNWSWWW|)|NN))|WWWNNWSSWN(SENNESNWSSWN|))))|N(WSNE|)E))))|W))|N))|W)|WWSS(WNNSSE|)E(N|S))))|W)|W))))|N)))|EE)|WNEN(W|E))|N)))|NNW(NEWS|)(S|W))|N))|W)|NWWNEE)|E)))|N)|S))))|S)|S)|W)|S)|W))|EE))|W(SS|N)))|NNN(N|E)))$";
}