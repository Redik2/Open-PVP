package main;

import arc.util.Log;
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
        String locale = player.locale.substring(0, 2);
        if (avivable_langs.contains(locale))
            key = code + "." + locale;
        else
        {
            key = code + ".en";
        }
        return localisation.get(key);
    }
    public static final Map<String, String> localisation = new HashMap<String, String>();
    static
    {
        localisation.put("respawnSpeedErr.ru", "[cyan] [#ff9000]\uE80A [#ff2000]Ограничение скорости возрождения! Осталось:  %d сек");
        localisation.put("respawnSpeedErr.en", "[cyan] [#ff9000]\uE80A [#ff2000]Respawn speed limit! Left:  %d sec");

        localisation.put("closeEnemyErr.ru", "[cyan] [#ff9000]\uE80A [#ff2000]Слишком близко к вражескому ядру");
        localisation.put("closeEnemyErr.en", "[cyan] [#ff9000]\uE80A [#ff2000]Too close to enemy core");

        localisation.put("votingTimeEnd.ru", "[cyan] [#78b193]Время голосования вышло!");
        localisation.put("votingTimeEnd.en", "[cyan] [#78b193]Voting time is over!");

        localisation.put("votingRestart.ru", "[cyan] [#78b193]Голосование окончено. Сервер перезагружается!");
        localisation.put("votingRestart.en", "[cyan] [#78b193]Voting finished. Server is restarting!");

        localisation.put("AcceptPlayer.ru", "[cyan] [#78b193]Вы были приняты в команду #%d");
        localisation.put("AcceptPlayer.en", "[cyan] [#78b193]You have been accepted into the team #%d");

        localisation.put("DenyPlayer.ru", "[cyan] [#78b193]Вы не были приняты в команду #%d");
        localisation.put("DenyPlayer.en", "[cyan] [#78b193]You have been denied into the team #%d");

        localisation.put("AcceptPlayerToTeam.ru", "[cyan] [#78b193]Запрос принят!");
        localisation.put("AcceptPlayerToTeam.en", "[cyan] [#78b193]Request accepted!");

        localisation.put("DenyPlayerToTeam.ru", "[cyan] [#78b193]Запрос не принят!");
        localisation.put("DenyPlayerToTeam.en", "[cyan] [#78b193]Request denied!");

        localisation.put("MaxUnitsAndItems.ru", "[#78b193]Максимум юнитов: [cyan]%d[white]\n[#78b193]Максимум предметов: [cyan]");
        localisation.put("MaxUnitsAndItems.en", "[#78b193]Unit limit: [cyan]%d[white]\n[#78b193]Item capacity: [cyan]");

        localisation.put("TimeLeft.ru", "[#78b193]До рестарта: [cyan]");
        localisation.put("TimeLeft.en", "[#78b193]Time left: [cyan]");

        localisation.put("Destroyed.ru", "[cyan] [#78b193]Блок уничтожен");
        localisation.put("Destroyed.en", "[cyan] [#78b193]Block destroyed");

        localisation.put("CantDestroy.ru", "[cyan] [#f]Вы не можете взорвать этот блок!");
        localisation.put("CantDestroy.en", "[cyan] [#f]You cant destroy this block!");

        localisation.put("NotID.ru", "[cyan] [#f]Это не ID!");
        localisation.put("NotID.en", "[cyan] [#f]This is not ID!");

        localisation.put("SpectatorTip.ru", "[cyan] [#f]Для вступления в спектаторы, напишите /spectate");
        localisation.put("SpectatorTip.en", "[cyan] [#f]Write /spectate to join spectators");

        localisation.put("NotActive.ru", "[cyan] [#f]Можно присоедениться только к активной команде");
        localisation.put("NotActive.en", "[cyan] [#f]You can join only active teams");

        localisation.put("SameTeam.ru", "[cyan] [#f]Вы уже в команде #%d");
        localisation.put("SameTeam.en", "[cyan] [#f]You already in team #%d");

        localisation.put("OnlySpectator.ru", "[cyan] [#f]Можно отправить запрос только если вы спектатор");
        localisation.put("OnlySpectator.en", "[cyan] [#f]Only if you are spectator");

        localisation.put("AlreadyHaveRequest.ru", "[cyan] [#f]У вас уже есть активный запрос");
        localisation.put("AlreadyHaveRequest.en", "[cyan] [#f]You already have an active request");

        localisation.put("RequestMessageToTeam.ru", "[cyan] [white]%s [#78b193]запрашивает присоеденение к вашей команде. Напишите [green]/accept[#78b193] или [red]/deny");
        localisation.put("RequestMessageToTeam.en", "[cyan] [white]%s [#78b193]want to join you. Write [green]/accept[#78b193] or [red]/deny");

        localisation.put("HaventAnyRequests.ru", "[cyan] [#f]У вас нет активных запросов на вступление");
        localisation.put("HaventAnyRequests.en", "[cyan] [#f]You havent any active requests");

        localisation.put("RequestSent.ru", "[cyan] [#78b193]Вы отправили запрос на вступление в команду #%d");
        localisation.put("RequestSent.en", "[cyan] [#78b193]You sent request to join team #%d");

        localisation.put("AlreadySpectate.ru", "[cyan] [#78b193]Вы уже спектатор!");
        localisation.put("AlreadySpectate.en", "[cyan] [#78b193]You are already spectator!");

        localisation.put("VotingScore.ru", "[cyan] [#78b193]%d/%d проголосовали за рестарт");
        localisation.put("VotingScore.en", "[cyan] [#78b193]%d/%d voted for restart");

        localisation.put("Voted.ru", "[cyan] [#78b193]Вы уже голосовали");
        localisation.put("Voted.en", "[cyan] [#78b193]You have already voted");

        localisation.put("InvalidFloor.ru", "[cyan] [#f]Недопустимый блок для размещения ядра");
        localisation.put("InvalidFloor.en", "[cyan] [#f]Invalid block to place core");

        localisation.put("YouWasKicked.ru", "[cyan] [#78b193]Лидер выгнал вас из команды");
        localisation.put("YouWasKicked.en", "[cyan] [#78b193]Leader kicked you from team");

        localisation.put("YouLeader.ru", "[cyan] [#78b193]Лидер передал вам права");
        localisation.put("YouLeader.en", "[cyan] [#78b193]Leader gave you rights");

        localisation.put("YouNotLeader.ru", "[cyan] [#78b193]Вы передали свои права");
        localisation.put("YouNotLeader.en", "[cyan] [#78b193]You gave your rights");

        localisation.put("YouKicked.ru", "[cyan] [#78b193]Вы выгнали игрока из команды");
        localisation.put("YouKicked.en", "[cyan] [#78b193]You kicked player from your team");

        localisation.put("Cancel.ru", "Отмена");
        localisation.put("Cancel.en", "Cancel");

        localisation.put("Back.ru", "Назад");
        localisation.put("Back.en", "Back");

        localisation.put("RulesB.ru", "Правила");
        localisation.put("RulesB.en", "Rules");

        localisation.put("Rules.ru", "• Не проявлять неуважительное отношение к игрокам\n" +
                "• Не ставить NSFW изображение любыми способами(сортировщики/дисплеи/холсты)\n" +
                "• Не засоряйте чат\n" +
                "• Все, что не запрещено - разрешено");
        localisation.put("Rules.en", "• Do not disrespect the players\n" +
                "• Do not put NSFW image in any way (sorters/displays/canvases)\n" +
                "• Do not clog the chat\n" +
                "• Everything that is not prohibited is allowed");

        localisation.put("CantBeLeader.ru", "[cyan] [#f]Передайте свои права другому игроку перед выходом из команды");
        localisation.put("CantBeLeader.en", "[cyan] [#f]Give your rights to another player before leave team");

        localisation.put("SpectatorError.ru", "[cyan] [#f]Не для спектаторов");
        localisation.put("SpectatorError.en", "[cyan] [#f]Not for spectators");

        localisation.put("LeaderError.ru", "[cyan] [#f]Вы не лидер команды");
        localisation.put("LeaderError.en", "[cyan] [#f]You are not team leader");

        localisation.put("KickYourself.ru", "[cyan] [#f]Нельзя выгнать самого себя");
        localisation.put("KickYourself.en", "[cyan] [#f]You cant kick yourself");

        localisation.put("AlreadyLeader.ru", "[cyan] [#f]Игрок уже лидер");
        localisation.put("AlreadyLeader.en", "[cyan] [#f]Player is already leader");

        localisation.put("Support.ru", "Поддержать сервер");
        localisation.put("Support.en", "Support server");

        localisation.put("Discord.ru", "Дискорд");
        localisation.put("Discord.en", "Discord");

        localisation.put("CoreSet.ru", "[cyan] [#78b193]Ядро установлено! [gray]Если вы не видите ядро, введите /sync");
        localisation.put("CoreSet.en", "[cyan] [#78b193]Core placed! [gray]If you cant see core, use /sync");

        localisation.put("VotingStart.ru", "[cyan] [#78b193]Начинается голосование за рестарт!\nНапишите [cyan]/restart[#78b193] чтобы проголосовать [cyan]за[#78b193] рестарт\nГолосование окончиться через 1мин");
        localisation.put("VotingStart.en", "[cyan] [#78b193]Started voting for restart!\nSend [cyan]/restart[#78b193] to vote [cyan]for[#78b193] restart\nVoting ends in 1min");

        localisation.put("Guide.ru", "[yellow]\uE807[] Ядро появиться в месте нажатия по карте\n" +
                "[yellow]\uE807[] Все хранилища заменяються на ядра-осколки\n" +
                "[red]\uE815[] /spectate уничтожит ваши ядра и позволяет выбрать другое место для ядра или просто сделает вас наблюдателем\n" +
                "[blue]\uE84D[] /join для вступления в команду\n" +
                "[blue]\uE84D[] /team для открытия меню команды\n" +
                "[green]\uE800[] Нажимая OK вы соглашаетесь с правилами");
        localisation.put("Guide.en", "[yellow]\uE807[] First core appears where you will press\n" +
                "[yellow]\uE807[] All vaults replaces by core: shard\n" +
                "[red]\uE815[] /spectate destroy your core and make you choose another spawn or just be spectator\n" +
                "[blue]\uE84D[] /join to open join menu\n" +
                "[blue]\uE84D[] /team to open team menu\n" +
                "[green]\uE800[] By clicking OK you agree to the rules");

    }
}
