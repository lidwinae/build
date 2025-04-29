# lidwinae's build collection

***

### Deskripsi Singkat
Aplikasi ini adalah sebuah Build Collection Manager yang memungkinkan pengguna untuk:
1. Melihat build collection yang ada pada "Available Collection" (made by lidwinae)
2. Menambahkan build collection yang sudah ada pada Available Collection ke build collection pribadi atau personal pada "Your Collection"
3. Menambahkan build baru dengan nama dan gambar baru (upload gambar) oleh pengguna yang juga menyediakan gambar template gambar1 dan gambar2 pada drawable jika pengguna tidak upload gambar
4. Menghapus build collection pribadi pada "Your Collection"

***

Aplikasi ini adalah upgrade dari https://github.com/lidwinae/buildcollection, di mana yang sebelumnya masih menggunakan ArrayList (dummy) kini sudah menggunakan database SQLite Room. Aplikasi ini hanya memungkinkan mode Portrait karena akan kurang cocok jika build ditampilkan secara Landscape. Aplikasi ini memiliki fitur CRUD lengkap (Create, Read, Update, dan Delete), di mana pengguna bisa menambahkan build baru (membuat build baru), melihat (menampilkan) list tampilan seluruh build yang ada, meng-update atau edit build yang dimiliki pengguna pada "Your Collection", serta menghapus build yang dimiliki pengguna.
