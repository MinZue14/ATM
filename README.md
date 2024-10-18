# ATM
khi run máy ảo:
b0: kiểm tra địa chỉ ip máy ảo: ip addr show

b1: terminal -> kiểm tra xampp đã bật MySQL chưa = lệnh :
cd /opt/lampp
sudo ./lampp status
	nếu chưa bật thì bật nó lên: sudo ./lampp start
	sau đó kiểm tra trạng thái: sudo ./lampp status
	running rồi thì nhập : sudo /opt/lampp/bin/mysql -u root -p
	ko nhập mk
sau đó gõ:  nhớ xóa your_password và đổi lại địa chỉ ip

CREATE USER 'root'@'192.168.1.18' IDENTIFIED BY 'your_password'; -- Nếu chưa tạo người dùng
GRANT ALL PRIVILEGES ON *.* TO 'root'@'192.168.1.18' WITH GRANT OPTION;
FLUSH PRIVILEGES;

b3: mở heidi:
nhập cổng của IP máy chủ 1

b4: nhập địa chỉ IP của máy chủ 1 trong file database

b5: run server và run login của client







