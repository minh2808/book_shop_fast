package com.example.BookShopFast.config;

import com.example.BookShopFast.entity.Book;
import com.example.BookShopFast.entity.Role;
import com.example.BookShopFast.entity.User;
import com.example.BookShopFast.repository.BookRepository;
import com.example.BookShopFast.repository.RoleRepository;
import com.example.BookShopFast.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra xem đã có dữ liệu chưa
        if (roleRepository.count() > 0) {
            System.out.println("Dữ liệu đã tồn tại, bỏ qua khởi tạo...");
            return;
        }

        System.out.println("Đang khởi tạo dữ liệu mẫu...");

        // Tạo Roles
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        adminRole = roleRepository.save(adminRole);

        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        userRole = roleRepository.save(userRole);

        // Tạo Users
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin123"); // Trong thực tế nên hash password
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEmail("admin@bookshop.com");
        admin.setPhone("0123456789");
        admin.setEnabled(true);
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        admin.setRoles(adminRoles);
        userRepository.save(admin);

        User customer = new User();
        customer.setUsername("customer");
        customer.setPassword("customer123"); // Trong thực tế nên hash password
        customer.setFirstName("Nguyễn");
        customer.setLastName("Văn A");
        customer.setEmail("customer@bookshop.com");
        customer.setPhone("0987654321");
        customer.setEnabled(true);
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);
        customer.setRoles(userRoles);
        userRepository.save(customer);

        // Tạo Books
        createSampleBooks();

        System.out.println("Khởi tạo dữ liệu mẫu hoàn tất!");
    }

    private void createSampleBooks() {
        // Sách Tiểu thuyết
        Book book1 = new Book();
        book1.setTitle("Nhà Giả Kim");
        book1.setAuthor("Paulo Coelho");
        book1.setPublisher("Nhà Xuất Bản Hội Nhà Văn");
        book1.setPublicationDate("1988");
        book1.setLanguage("Tiếng Việt");
        book1.setCategory("Tiểu thuyết");
        book1.setNumberOfPages(192);
        book1.setFormat("Bìa mềm");
        book1.setIsbn(978604306);
        book1.setShippingWeight(0.2);
        book1.setListPrice(120000);
        book1.setOurPrice(96000);
        book1.setActive(true);
        book1.setDescription("Nhà Giả Kim là một cuốn tiểu thuyết nổi tiếng của nhà văn Paulo Coelho, kể về hành trình tìm kiếm kho báu và ý nghĩa cuộc sống của một cậu bé chăn cừu.");
        book1.setInStockNumber(50);
        book1.setImageUrl("https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1436202607i/3735293.jpg");
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setTitle("Đắc Nhân Tâm");
        book2.setAuthor("Dale Carnegie");
        book2.setPublisher("Nhà Xuất Bản Trẻ");
        book2.setPublicationDate("1936");
        book2.setLanguage("Tiếng Việt");
        book2.setCategory("Self-help");
        book2.setNumberOfPages(320);
        book2.setFormat("Bìa mềm");
        book2.setIsbn(978604100);
        book2.setShippingWeight(0.3);
        book2.setListPrice(89000);
        book2.setOurPrice(71200);
        book2.setActive(true);
        book2.setDescription("Đắc Nhân Tâm là cuốn sách nổi tiếng về nghệ thuật ứng xử và giao tiếp, giúp bạn xây dựng mối quan hệ tốt đẹp với mọi người.");
        book2.setInStockNumber(100);
        book2.setImageUrl("https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1436202607i/3735293.jpg");
        bookRepository.save(book2);

        Book book3 = new Book();
        book3.setTitle("Sapiens: Lược Sử Loài Người");
        book3.setAuthor("Yuval Noah Harari");
        book3.setPublisher("Nhà Xuất Bản Trẻ");
        book3.setPublicationDate("2011");
        book3.setLanguage("Tiếng Việt");
        book3.setCategory("Lịch sử");
        book3.setNumberOfPages(512);
        book3.setFormat("Bìa cứng");
        book3.setIsbn(978604101);
        book3.setShippingWeight(0.8);
        book3.setListPrice(250000);
        book3.setOurPrice(200000);
        book3.setActive(true);
        book3.setDescription("Cuốn sách kể về lịch sử tiến hóa của loài người từ thời kỳ đồ đá đến hiện đại, với góc nhìn độc đáo và sâu sắc.");
        book3.setInStockNumber(30);
        book3.setImageUrl("https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1436202607i/3735293.jpg");
        bookRepository.save(book3);

        Book book4 = new Book();
        book4.setTitle("Tôi Tài Giỏi, Bạn Cũng Thế");
        book4.setAuthor("Adam Khoo");
        book4.setPublisher("Nhà Xuất Bản Trẻ");
        book4.setPublicationDate("2009");
        book4.setLanguage("Tiếng Việt");
        book4.setCategory("Self-help");
        book4.setNumberOfPages(256);
        book4.setFormat("Bìa mềm");
        book4.setIsbn(978604102);
        book4.setShippingWeight(0.25);
        book4.setListPrice(85000);
        book4.setOurPrice(68000);
        book4.setActive(true);
        book4.setDescription("Cuốn sách chia sẻ các phương pháp học tập hiệu quả và kỹ năng quản lý thời gian để đạt thành công.");
        book4.setInStockNumber(75);
        book4.setImageUrl("https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1436202607i/3735293.jpg");
        bookRepository.save(book4);

        Book book5 = new Book();
        book5.setTitle("Doraemon - Tập 1");
        book5.setAuthor("Fujiko F. Fujio");
        book5.setPublisher("Nhà Xuất Bản Kim Đồng");
        book5.setPublicationDate("1969");
        book5.setLanguage("Tiếng Việt");
        book5.setCategory("Thiếu nhi");
        book5.setNumberOfPages(192);
        book5.setFormat("Bìa mềm");
        book5.setIsbn(978604200);
        book5.setShippingWeight(0.15);
        book5.setListPrice(35000);
        book5.setOurPrice(28000);
        book5.setActive(true);
        book5.setDescription("Truyện tranh Doraemon nổi tiếng với những câu chuyện vui nhộn và ý nghĩa về chú mèo máy đến từ tương lai.");
        book5.setInStockNumber(200);
        book5.setImageUrl("https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1436202607i/3735293.jpg");
        bookRepository.save(book5);

        Book book6 = new Book();
        book6.setTitle("Rich Dad Poor Dad");
        book6.setAuthor("Robert Kiyosaki");
        book6.setPublisher("Nhà Xuất Bản Trẻ");
        book6.setPublicationDate("1997");
        book6.setLanguage("Tiếng Việt");
        book6.setCategory("Kinh doanh");
        book6.setNumberOfPages(336);
        book6.setFormat("Bìa mềm");
        book6.setIsbn(978604103);
        book6.setShippingWeight(0.35);
        book6.setListPrice(120000);
        book6.setOurPrice(96000);
        book6.setActive(true);
        book6.setDescription("Cuốn sách về tài chính cá nhân và đầu tư, dạy cách suy nghĩ về tiền bạc và xây dựng tài sản.");
        book6.setInStockNumber(60);
        book6.setImageUrl("https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1436202607i/3735293.jpg");
        bookRepository.save(book6);

        Book book7 = new Book();
        book7.setTitle("Vũ Trụ Trong Lòng Bàn Tay");
        book7.setAuthor("Neil deGrasse Tyson");
        book7.setPublisher("Nhà Xuất Bản Trẻ");
        book7.setPublicationDate("2017");
        book7.setLanguage("Tiếng Việt");
        book7.setCategory("Khoa học");
        book7.setNumberOfPages(288);
        book7.setFormat("Bìa mềm");
        book7.setIsbn(978604104);
        book7.setShippingWeight(0.3);
        book7.setListPrice(150000);
        book7.setOurPrice(120000);
        book7.setActive(true);
        book7.setDescription("Khám phá những bí ẩn của vũ trụ qua góc nhìn của nhà vật lý thiên văn nổi tiếng Neil deGrasse Tyson.");
        book7.setInStockNumber(40);
        book7.setImageUrl("https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1436202607i/3735293.jpg");
        bookRepository.save(book7);

        Book book8 = new Book();
        book8.setTitle("Lịch Sử Việt Nam");
        book8.setAuthor("Nhiều tác giả");
        book8.setPublisher("Nhà Xuất Bản Giáo Dục");
        book8.setPublicationDate("2020");
        book8.setLanguage("Tiếng Việt");
        book8.setCategory("Lịch sử");
        book8.setNumberOfPages(600);
        book8.setFormat("Bìa cứng");
        book8.setIsbn(978604105);
        book8.setShippingWeight(1.0);
        book8.setListPrice(300000);
        book8.setOurPrice(240000);
        book8.setActive(true);
        book8.setDescription("Cuốn sách tổng hợp về lịch sử Việt Nam từ thời kỳ dựng nước đến hiện đại.");
        book8.setInStockNumber(25);
        book8.setImageUrl("https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1436202607i/3735293.jpg");
        bookRepository.save(book8);

        Book book9 = new Book();
        book9.setTitle("Harry Potter và Hòn Đá Phù Thủy");
        book9.setAuthor("J.K. Rowling");
        book9.setPublisher("Nhà Xuất Bản Trẻ");
        book9.setPublicationDate("1997");
        book9.setLanguage("Tiếng Việt");
        book9.setCategory("Tiểu thuyết");
        book9.setNumberOfPages(320);
        book9.setFormat("Bìa mềm");
        book9.setIsbn(978604106);
        book9.setShippingWeight(0.4);
        book9.setListPrice(150000);
        book9.setOurPrice(120000);
        book9.setActive(true);
        book9.setDescription("Cuốn đầu tiên trong series Harry Potter nổi tiếng, kể về cậu bé phù thủy và cuộc phiêu lưu tại trường Hogwarts.");
        book9.setInStockNumber(80);
        book9.setImageUrl("https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1436202607i/3735293.jpg");
        bookRepository.save(book9);

        Book book10 = new Book();
        book10.setTitle("Tư Duy Nhanh và Chậm");
        book10.setAuthor("Daniel Kahneman");
        book10.setPublisher("Nhà Xuất Bản Trẻ");
        book10.setPublicationDate("2011");
        book10.setLanguage("Tiếng Việt");
        book10.setCategory("Khoa học");
        book10.setNumberOfPages(608);
        book10.setFormat("Bìa mềm");
        book10.setIsbn(978604107);
        book10.setShippingWeight(0.6);
        book10.setListPrice(220000);
        book10.setOurPrice(176000);
        book10.setActive(true);
        book10.setDescription("Cuốn sách về tâm lý học nhận thức, giải thích cách bộ não đưa ra quyết định và những sai lầm trong tư duy.");
        book10.setInStockNumber(35);
        book10.setImageUrl("https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1436202607i/3735293.jpg");
        bookRepository.save(book10);

        Book book11 = new Book();
        book11.setTitle("Khởi Nghiệp Tinh Gọn");
        book11.setAuthor("Eric Ries");
        book11.setPublisher("Nhà Xuất Bản Trẻ");
        book11.setPublicationDate("2011");
        book11.setLanguage("Tiếng Việt");
        book11.setCategory("Kinh doanh");
        book11.setNumberOfPages(336);
        book11.setFormat("Bìa mềm");
        book11.setIsbn(978604108);
        book11.setShippingWeight(0.35);
        book11.setListPrice(140000);
        book11.setOurPrice(112000);
        book11.setActive(true);
        book11.setDescription("Phương pháp khởi nghiệp hiện đại với cách tiếp cận tinh gọn, giảm thiểu rủi ro và tối ưu hóa nguồn lực.");
        book11.setInStockNumber(45);
        book11.setImageUrl("https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1436202607i/3735293.jpg");
        bookRepository.save(book11);

        Book book12 = new Book();
        book12.setTitle("Conan - Tập 1");
        book12.setAuthor("Gosho Aoyama");
        book12.setPublisher("Nhà Xuất Bản Kim Đồng");
        book12.setPublicationDate("1994");
        book12.setLanguage("Tiếng Việt");
        book12.setCategory("Thiếu nhi");
        book12.setNumberOfPages(192);
        book12.setFormat("Bìa mềm");
        book12.setIsbn(978604201);
        book12.setShippingWeight(0.15);
        book12.setListPrice(35000);
        book12.setOurPrice(28000);
        book12.setActive(true);
        book12.setDescription("Truyện tranh trinh thám nổi tiếng về thám tử nhí Conan Edogawa và những vụ án ly kỳ.");
        book12.setInStockNumber(150);
        book12.setImageUrl("https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1436202607i/3735293.jpg");
        bookRepository.save(book12);
    }
}

