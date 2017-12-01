package bean;

import java.util.ArrayList;
import java.util.List;

public class FlowerList {
    private List<Flower> flowers;

    public FlowerList() {
        flowers = new ArrayList<>();
    }

    public void setFlowers(List<Flower> flowers) {
        this.flowers = flowers;
    }

    public List<Flower> getFlowers() {
        return flowers;
    }
}
