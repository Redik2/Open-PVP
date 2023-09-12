package main;

import mindustry.gen.Groups;
import mindustry.gen.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Localisation {
    public static List<String> avivable_langs = new ArrayList<String>();
    static
    {
        avivable_langs.add("ru");
        avivable_langs.add("en");
    }
    public static String local(Player player, String code)
    {
        String key = "";
        if (avivable_langs.contains(player.locale))
            key = code + "_" + player.locale;
        else
        {
            key = code + "_en";
        }
        return localisation.get(key);
    }
    public static final Map<String, String> localisation = new HashMap<String, String>();
    static
    {
        localisation.put("respawnSpeedErr_ru", "[gray]<[cyan]SERVER[gray]> [#ff9000]\uE80A [#ff2000]Ограничение скорости возрождения! Осталось:  %d сек");
        localisation.put("respawnSpeedErr_en", "[gray]<[cyan]SERVER[gray]> [#ff9000]\uE80A [#ff2000]Respawn speed limit! Left:  %d sec");

        localisation.put("closeEnemyErr_ru", "[gray]<[cyan]SERVER[gray]> [#ff9000]\uE80A [#ff2000]Слишком близко к вражескому ядру");
        localisation.put("closeEnemyErr_en", "[gray]<[cyan]SERVER[gray]> [#ff9000]\uE80A [#ff2000]Too close to enemy core");

        localisation.put("votingTimeEnd_ru", "[gray]<[cyan]SERVER[gray]> [#78b193]Время голосования вышло!");
        localisation.put("votingTimeEnd_en", "[gray]<[cyan]SERVER[gray]> [#78b193]Voting time is over!");

        localisation.put("votingRestart_ru", "[gray]<[cyan]SERVER[gray]> [#78b193]Голосование окончено. Сервер перезагружается!");
        localisation.put("votingRestart_en", "[gray]<[cyan]SERVER[gray]> [#78b193]Voting finished. Server is restarting!");

        localisation.put("AcceptPlayer_ru", "[gray]<[cyan]SERVER[gray]> [#78b193]Вы были приняты в команду #%d");
        localisation.put("AcceptPlayer_en", "[gray]<[cyan]SERVER[gray]> [#78b193]You have been accepted into the team #%d");

        localisation.put("DenyPlayer_ru", "[gray]<[cyan]SERVER[gray]> [#78b193]Вы не были приняты в команду #%d");
        localisation.put("DenyPlayer_en", "[gray]<[cyan]SERVER[gray]> [#78b193]You have been denied into the team #%d");

        localisation.put("AcceptPlayerToTeam_ru", "[gray]<[cyan]SERVER[gray]> [#78b193]Запрос принят!");
        localisation.put("AcceptPlayerToTeam_en", "[gray]<[cyan]SERVER[gray]> [#78b193]Request accepted!");

        localisation.put("DenyPlayerToTeam_ru", "[gray]<[cyan]SERVER[gray]> [#78b193]Запрос не принят!");
        localisation.put("DenyPlayerToTeam_en", "[gray]<[cyan]SERVER[gray]> [#78b193]Request denied!");

        localisation.put("MaxUnitsAndItems_ru", "[#78b193]Максимум юнитов: [cyan]%d[white]\n[#78b193]Максимум предметов: [cyan]");
        localisation.put("MaxUnitsAndItems_en", "[#78b193]Unit limit: [cyan]%d[white]\n[#78b193]Item capacity: [cyan]");

        localisation.put("TimeLeft_ru", "[#78b193]До рестарта: [cyan]");
        localisation.put("TimeLeft_en", "[#78b193]Time left: [cyan]");

        localisation.put("Destroyed_ru", "[gray]<[cyan]SERVER[gray]> [#78b193]Блок уничтожен");
        localisation.put("Destroyed_en", "[gray]<[cyan]SERVER[gray]> [#78b193]Block destroyed");

        localisation.put("CantDestroy_ru", "[gray]<[cyan]SERVER[gray]> [#f]Вы не можете взорвать этот блок!");
        localisation.put("CantDestroy_en", "[gray]<[cyan]SERVER[gray]> [#f]You cant destroy this block!");

        localisation.put("NotID_ru", "[gray]<[cyan]SERVER[gray]> [#f]Это не ID!");
        localisation.put("NotID_en", "[gray]<[cyan]SERVER[gray]> [#f]This is not ID!");

        localisation.put("SpectatorTip_ru", "[gray]<[cyan]SERVER[gray]> [#f]Для вступления в спектаторы, напишите /spectate");
        localisation.put("SpectatorTip_en", "[gray]<[cyan]SERVER[gray]> [#f]Write /spectate to join spectators");

        localisation.put("NotActive_ru", "[gray]<[cyan]SERVER[gray]> [#f]Можно присоедениться только к активной команде");
        localisation.put("NotActive_en", "[gray]<[cyan]SERVER[gray]> [#f]You can join only active teams");

        localisation.put("SameTeam_ru", "[gray]<[cyan]SERVER[gray]> [#f]Вы уже в команде #%d");
        localisation.put("SameTeam_en", "[gray]<[cyan]SERVER[gray]> [#f]You already in team #%d");

        localisation.put("OnlySpectator_ru", "[gray]<[cyan]SERVER[gray]> [#f]Можно отправить запрос только если вы спектатор");
        localisation.put("OnlySpectator_en", "[gray]<[cyan]SERVER[gray]> [#f]Only if you are spectator");

        localisation.put("AlreadyHaveRequest_ru", "[gray]<[cyan]SERVER[gray]> [#f]У вас уже есть активный запрос");
        localisation.put("AlreadyHaveRequest_en", "[gray]<[cyan]SERVER[gray]> [#f]You already have an active request");

        localisation.put("RequestMessageToTeam_ru", "[gray]<[cyan]SERVER[gray]> [white]%s [#78b193]запрашивает присоеденение к вашей команде. Любой ее участник может написать [green]/accept[#78b193] или [red]/deny");
        localisation.put("RequestMessageToTeam_en", "[gray]<[cyan]SERVER[gray]> [white]%s [#78b193]want to join you. Any member of your team can write [green]/accept[#78b193] or [red]/deny");

        localisation.put("HaventAnyRequests_ru", "[gray]<[cyan]SERVER[gray]> [#f]У вас нет активных запросов на вступление");
        localisation.put("HaventAnyRequests_en", "[gray]<[cyan]SERVER[gray]> [#f]You havent any active requests");

        localisation.put("RequestSent_ru", "[gray]<[cyan]SERVER[gray]> [#78b193]Вы отправили запрос на вступление в команду #%d");
        localisation.put("RequestSent_en", "[gray]<[cyan]SERVER[gray]> [#78b193]You sent request to join team #%d");

        localisation.put("AlreadySpectate_ru", "[gray]<[cyan]SERVER[gray]> [#78b193]Вы уже спектатор!");
        localisation.put("AlreadySpectate_en", "[gray]<[cyan]SERVER[gray]> [#78b193]You are already spectator!");

        localisation.put("VotingScore_ru", "[gray]<[cyan]SERVER[gray]> [#78b193]%d/%d проголосовали за рестарт");
        localisation.put("VotingScore_en", "[gray]<[cyan]SERVER[gray]> [#78b193]%d/%d voted for restart");

        localisation.put("Voted_ru", "[gray]<[cyan]SERVER[gray]> [#78b193]Вы уже голосовали");
        localisation.put("Voted_en", "[gray]<[cyan]SERVER[gray]> [#78b193]You have already voted");

        localisation.put("InvalidFloor_ru", "[gray]<[cyan]SERVER[gray]> [#f]Недопустимый блок для размещения ядра");
        localisation.put("InvalidFloor_en", "[gray]<[cyan]SERVER[gray]> [#f]Invalid block to place core");

        localisation.put("VotingStart_ru", "[gray]<[cyan]SERVER[gray]> [#78b193]Начинается голосование за рестарт!\nНапишите [cyan]/restart[#78b193] чтобы проголосовать [cyan]за[#78b193] рестарт\nГолосование окончиться через 1мин");
        localisation.put("VotingStart_en", "[gray]<[cyan]SERVER[gray]> [#78b193]Started voting for restart!\nSend [cyan]/restart[#78b193] to vote [cyan]for[#78b193] restart\nVoting ends in 1min");



        localisation.put("Guide_ru", "[yellow]\uE807[] Ядро появиться в месте нажатия по карте\n" +
                "[red]\uE815[] /spectate уничтожит ваши ядра и позволяет выбрать другое место для ядра или просто сделает вас наблюдателем\n" +
                "[blue]\uE84D[] /join <team id> для вступления в команду по ID\n" +
                "[blue]\uE84D[] /team чтобы узнать ID вашей команды");
        localisation.put("Guide_en", "[yellow]\uE807[] First core appears where you will press\n" +
                "[red]\uE815[] /spectate destroy your core and make you choose another spawn or just be spectator\n" +
                "[blue]\uE84D[] /join <team id> to join team by ID\n" +
                "[blue]\uE84D[] /team to get your team ID");

    }
}
