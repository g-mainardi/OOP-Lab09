package it.unibo.oop.lab.lambda.ex02;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return this.songs.stream()
                .map(song -> song.getSongName())
                .sorted((name1, name2) -> {
                    return name1.compareTo(name2);
                });
    }

    @Override
    public Stream<String> albumNames() {
        return albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return albums.entrySet().stream()
            .filter(entry -> entry.getValue() == year)
            .map(entry -> entry.getKey());
    }

    @Override
    public int countSongs(final String albumName) {
        return (int) this.songs.stream()
                .filter(song -> song.getAlbumName().isPresent())
                .map(song -> song.getAlbumName())
                .filter(album -> album.equals(Optional.ofNullable(albumName)))
                .count();
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) this.songs.stream()
                .map(song -> song.getAlbumName())
                .filter(album -> album.isEmpty())
                .count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return OptionalDouble.of(this.songs.stream()
                                .filter(song -> song.albumName.equals(Optional.ofNullable(albumName)))
                                .map(song -> song.getDuration())
                                .reduce((s1, s2) -> s1 + s2).orElse(1.0) / countSongs(albumName));
    }

    @Override
    public Optional<String> longestSong() {
        return this.songs.stream()
                .max((s1, s2) -> {
                    return Double.compare(s1.getDuration(), s2.getDuration());
                })
                .map(song -> song.getSongName());
    }

    @Override
    public Optional<String> longestAlbum() {
        final Map<Optional<String>, Double> map = new HashMap<>(); 
        this.songs.stream()
            .forEach(t -> {
                map.merge(t.getAlbumName(), t.getDuration(), (old, current) -> {
                    return old + current;
                });
            });
        return map.entrySet().stream().max((e1, e2) -> Double.compare(e1.getValue(), e2.getValue())).get().getKey();
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
