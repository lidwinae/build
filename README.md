# lidwinae's build collection

***

### Deskripsi Singkat
Aplikasi ini adalah sebuah Build Collection Manager yang memungkinkan pengguna untuk:
1. Melihat build collection yang ada pada "Available Collection" (made by lidwinae)
2. Menambahkan build collection yang sudah ada pada Available Collection ke build collection pribadi atau personal pada "Your Collection"
3. Menambahkan build baru dengan nama yang berbeda dan gambar template yang sudah disediakan: gambar1 dan gambar2 pada drawable
4. Menghapus build collection pribadi pada "Your Collection"

***

Aplikasi ini menerapkan 2 recycler view untuk "Available Collection" yang datanya ditampilkan secara horizontal (kiri kanan) dan "Your Collection" yang datanya ditampilkan secara vertikal (atas bawah). Terdapat fitur Create, Read, dan Delete yang sudah terhubung dengan database SQLite. Aplikasi ini sama persis dengan https://github.com/lidwinae/buildcollection, namun tidak lagi menggunakan ArrayList, tetapi database SQLite Open Helper lebih tepatnya.
