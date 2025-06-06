package dat.entities;

import dat.entities.enums.StrategyType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "strategies")
public class Strategy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private boolean teamId;

    @Enumerated(EnumType.STRING)
    private StrategyType type;

    @ManyToMany(mappedBy = "strategies", fetch = FetchType.EAGER)
    private Set<Map> maps = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;


    public Strategy() {
    }

    public Strategy(String title, String description, Boolean teamId, StrategyType type) {
        this.title = title;
        this.description = description;
        this.teamId = teamId;
        this.type = type;
    }
}
