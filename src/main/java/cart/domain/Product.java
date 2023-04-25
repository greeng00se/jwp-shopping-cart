package cart.domain;

public class Product {

    private static final int MAX_NAME_LENGTH = 100;
    private static final int MIN_PRICE_VALUE = 0;

    private final Long id;
    private final String name;
    private final String image;
    private final long price;

    public Product(final String name, final String image, final long price) {
        this(null, name, image, price);
    }

    public Product(final Long id, final String name, final String image, final long price) {
        validateName(name);
        validatePrice(price);
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
    }

    private void validateName(final String name) {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("상품명의 길이는 " + MAX_NAME_LENGTH + "자 이하여야합니다.");
        }
    }

    private void validatePrice(final long price) {
        if (price < MIN_PRICE_VALUE) {
            throw new IllegalArgumentException("상품 가격은 " + MIN_PRICE_VALUE + "원 이상이여야 합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public long getPrice() {
        return price;
    }
}