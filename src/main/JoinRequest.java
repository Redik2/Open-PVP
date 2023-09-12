package main;

public class JoinRequest
{
    public String player;
    public Integer time;
    public Integer status;

    public JoinRequest(String uuid, Integer time)
    {
        this.player = uuid;
        this.time = time;
        this.status = 0;
    }
}
