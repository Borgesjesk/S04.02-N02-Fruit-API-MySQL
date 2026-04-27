package cat.itacademy.s04.t02.n02.fruit_api_mysql.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "providers")
@Getter
@Setter
@NoArgsConstructor
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String country;

    public Provider(String name, String country) {
        this.name = name;
        this.country = country;
    }
}