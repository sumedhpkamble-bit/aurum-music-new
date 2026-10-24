import React, { useState, useEffect } from 'react';
import {
  Play, Pause, SkipBack, SkipForward, Shuffle, Repeat, Volume2,
  Sliders, Music, Folder, ListMusic, Heart, Search, Settings,
  Download, Smartphone, Terminal, Github, CheckCircle2,
  FileCode, Sparkles, RotateCcw, RotateCw, Headphones, User,
  Disc, X, Mic2, Radio, AudioLines, Lock, Unlock, Bluetooth,
  Layers, ChevronRight, Bookmark, Flame, Library
} from 'lucide-react';

interface SongItem {
  id: number;
  title: string;
  artist: string;
  album: string;
  genre: string;
  duration: string;
  durationSec: number;
  isFavorite: boolean;
  coverColor: string;
}

const SAMPLE_SONGS: SongItem[] = [
  { id: 1, title: 'Midnight Resonance', artist: 'Aurum Odyssey', album: 'Violet Dreams', genre: 'Hi-Fi & Acoustic', duration: '3:45', durationSec: 225, isFavorite: true, coverColor: 'from-purple-900 to-indigo-950' },
  { id: 2, title: 'Neon Highway', artist: 'Kavinsky & Synthwave', album: 'Nightfall Pulse', genre: 'Synthwave', duration: '4:12', durationSec: 252, isFavorite: true, coverColor: 'from-violet-800 to-purple-950' },
  { id: 3, title: 'Deep Submerged', artist: 'Sonar Bass Collective', album: 'Low Frequency', genre: 'Bass & Low-End', duration: '3:18', durationSec: 198, isFavorite: false, coverColor: 'from-purple-950 to-neutral-900' },
  { id: 4, title: 'Cybernetic Echoes', artist: 'Quantum Phase', album: 'Digital Horizon', genre: 'Electronic', duration: '5:02', durationSec: 302, isFavorite: true, coverColor: 'from-fuchsia-900 to-purple-900' },
  { id: 5, title: 'Astral Flow', artist: 'Aurum Odyssey', album: 'Starlight Sessions', genre: 'Ambient', duration: '2:54', durationSec: 174, isFavorite: false, coverColor: 'from-violet-900 to-black' },
  { id: 6, title: 'Golden Hour Requiem', artist: 'Aura Ensemble', album: 'Hi-Fi Masterworks', genre: 'Classical / Instrumental', duration: '4:30', durationSec: 270, isFavorite: true, coverColor: 'from-amber-900 to-purple-950' },
  { id: 7, title: 'Pulse Resonance', artist: 'Kavinsky & Synthwave', album: 'Nightfall Pulse', genre: 'Synthwave', duration: '3:30', durationSec: 210, isFavorite: false, coverColor: 'from-purple-950 to-violet-900' },
  { id: 8, title: 'Crystal Harmonics', artist: 'Aura Ensemble', album: 'Hi-Fi Masterworks', genre: 'Classical / Instrumental', duration: '4:15', durationSec: 255, isFavorite: true, coverColor: 'from-indigo-900 to-purple-950' }
];

const PRESETS = ['Flat', 'Bass Boost', 'Rock', 'Pop', 'Jazz', 'Electronic', 'Vocal Boost', 'Classical'];

// The Iconic Suggested Vector Logo: Violet Headphones + Golden Note + Equalizer Waveform Bars
function AurumMusicLogo({ size = 40 }: { size?: number }) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 128 128"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      className="flex-shrink-0 drop-shadow-[0_0_15px_rgba(139,92,246,0.6)]"
    >
      {/* Background Circle */}
      <circle cx="64" cy="64" r="60" fill="#0E0A1A" />
      {/* Outer Glow Ring */}
      <circle cx="64" cy="64" r="52" stroke="#8B5CF6" strokeWidth="3.5" />
      {/* Headphone Arch */}
      <path
        d="M34 68C34 42 46 30 64 30C82 30 94 42 94 68"
        stroke="#C084FC"
        strokeWidth="5"
        strokeLinecap="round"
      />
      {/* Left Ear Cup */}
      <rect x="24" y="64" width="16" height="26" rx="4" fill="#8B5CF6" />
      {/* Right Ear Cup */}
      <rect x="88" y="64" width="16" height="26" rx="4" fill="#8B5CF6" />
      {/* Golden Music Note Centerpiece */}
      <path
        d="M58 46L78 41V68A8 8 0 1 1 70 60A8 8 0 0 1 74 61V48L62 51V74A8 8 0 1 1 54 66A8 8 0 0 1 58 67V46Z"
        fill="#FACC15"
      />
      {/* Equalizer Sound Wave Bars at Bottom */}
      <path d="M48 98V104" stroke="#FACC15" strokeWidth="3" strokeLinecap="round" />
      <path d="M56 94V108" stroke="#FACC15" strokeWidth="3" strokeLinecap="round" />
      <path d="M64 90V112" stroke="#FACC15" strokeWidth="3.5" strokeLinecap="round" />
      <path d="M72 94V108" stroke="#FACC15" strokeWidth="3" strokeLinecap="round" />
      <path d="M80 98V104" stroke="#FACC15" strokeWidth="3" strokeLinecap="round" />
    </svg>
  );
}

export default function App() {
  const [activeTab, setActiveTab] = useState<'preview' | 'code' | 'build-guide'>('preview');
  const [simScreen, setSimScreen] = useState<'home' | 'songs' | 'library' | 'nowPlaying' | 'equalizer' | 'search' | 'lockScreen'>('home');

  // Player state
  const [songs, setSongs] = useState<SongItem[]>(SAMPLE_SONGS);
  const [currentSongIndex, setCurrentSongIndex] = useState<number>(0);
  const [isPlaying, setIsPlaying] = useState<boolean>(true);
  const [playbackSec, setPlaybackSec] = useState<number>(45);
  const [isShuffle, setIsShuffle] = useState<boolean>(false);
  const [repeatMode, setRepeatMode] = useState<'off' | 'all' | 'one'>('all');
  const [repeatToast, setRepeatToast] = useState<string>('');

  // Library classification tab inside Home screen: 'playlists' | 'genre' | 'artist'
  const [librarySubTab, setLibrarySubTab] = useState<'playlists' | 'genre' | 'artist'>('playlists');
  const [searchQuery, setSearchQuery] = useState<string>('');

  // Equalizer state
  const [eqPreset, setEqPreset] = useState<string>('Bass Boost');
  const [eqEnabled, setEqEnabled] = useState<boolean>(true);
  const [bassStrength, setBassStrength] = useState<number>(80);
  const [virtualizerStrength, setVirtualizerStrength] = useState<number>(65);
  const [bands, setBands] = useState<number[]>([6, 4, 0, 2, -1]);

  const currentSong = songs[currentSongIndex] || songs[0];
  const [selectedFile, setSelectedFile] = useState<string>('app/src/main/res/drawable/ic_aurum_logo.xml');

  // Playback timer & Repeat One logic
  useEffect(() => {
    let interval: any = null;
    if (isPlaying) {
      interval = setInterval(() => {
        setPlaybackSec(prev => {
          if (prev >= currentSong.durationSec) {
            if (repeatMode === 'one') {
              // Repeat One: restart current track
              return 0;
            }
            if (repeatMode === 'all') {
              setCurrentSongIndex(i => (i + 1) % songs.length);
              return 0;
            }
            setIsPlaying(false);
            return prev;
          }
          return prev + 1;
        });
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [isPlaying, currentSong, repeatMode, songs.length]);

  const toggleFavorite = (id: number) => {
    setSongs(prev => prev.map(s => s.id === id ? { ...s, isFavorite: !s.isFavorite } : s));
  };

  const handleToggleRepeat = () => {
    setRepeatMode(prev => {
      if (prev === 'off') {
        showToast('Repeat All Enabled');
        return 'all';
      }
      if (prev === 'all') {
        showToast('🔂 Repeat ONE Track Enabled');
        return 'one';
      }
      showToast('Repeat Disabled');
      return 'off';
    });
  };

  const showToast = (msg: string) => {
    setRepeatToast(msg);
    setTimeout(() => setRepeatToast(''), 2200);
  };

  const formatSeconds = (sec: number) => {
    const m = Math.floor(sec / 60);
    const s = sec % 60;
    return `${m}:${s < 10 ? '0' : ''}${s}`;
  };

  const progressPercent = (playbackSec / currentSong.durationSec) * 100;

  // Group songs by Genre
  const songsByGenre = songs.reduce((acc, song) => {
    if (!acc[song.genre]) acc[song.genre] = [];
    acc[song.genre].push(song);
    return acc;
  }, {} as Record<string, SongItem[]>);

  // Group songs by Artist
  const songsByArtist = songs.reduce((acc, song) => {
    if (!acc[song.artist]) acc[song.artist] = [];
    acc[song.artist].push(song);
    return acc;
  }, {} as Record<string, SongItem[]>);

  // Playlists
  const samplePlaylists = [
    { id: -1, name: 'Starred Favorites', desc: `${songs.filter(s => s.isFavorite).length} tracks`, songs: songs.filter(s => s.isFavorite) },
    { id: -2, name: 'Recently Played', desc: '5 tracks in history', songs: songs.slice(0, 5) },
    { id: 1, name: 'Late Night Chill & Lo-Fi', desc: '3 ambient tracks', songs: songs.filter(s => s.genre.includes('Ambient') || s.genre.includes('Hi-Fi')) },
    { id: 2, name: 'Neon Drive Workout', desc: 'High energy synth & bass', songs: songs.filter(s => s.genre.includes('Synthwave') || s.genre.includes('Bass')) }
  ];

  return (
    <div className="min-h-screen bg-[#07060A] text-[#F3F4F6] font-sans flex flex-col">
      {/* Top Banner Navigation */}
      <header className="border-b border-[#251A3E] bg-[#0E0A1A]/90 backdrop-blur-md sticky top-0 z-50 px-6 py-3 flex items-center justify-between">
        <div className="flex items-center space-x-3.5">
          {/* Restored Iconic Vector Logo */}
          <AurumMusicLogo size={42} />

          <div>
            <div className="flex items-center space-x-2">
              <h1 className="font-extrabold text-lg tracking-tight bg-gradient-to-r from-white via-purple-100 to-amber-300 bg-clip-text text-transparent">
                Aurum Music
              </h1>
              <span className="px-2 py-0.5 text-[10px] font-semibold bg-purple-950/80 text-purple-300 border border-purple-700/60 rounded-full flex items-center space-x-1">
                <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse" />
                <span>Native Android</span>
              </span>
            </div>
            <p className="text-xs text-neutral-400">Media3 ExoPlayer • Lock Screen Live Widget • Room Database</p>
          </div>
        </div>

        {/* Action Controls & Navigation Tabs */}
        <div className="flex items-center space-x-3">
          {/* Quick Lock Screen Toggle Button */}
          <button
            onClick={() => setSimScreen(s => s === 'lockScreen' ? 'home' : 'lockScreen')}
            className={`flex items-center space-x-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold border transition ${
              simScreen === 'lockScreen'
                ? 'bg-amber-400/20 text-amber-300 border-amber-400/60 shadow-[0_0_12px_rgba(250,204,21,0.3)]'
                : 'bg-[#180E2B] text-neutral-300 border-purple-900/50 hover:border-purple-500'
            }`}
          >
            <Lock className="w-3.5 h-3.5 text-amber-400" />
            <span>{simScreen === 'lockScreen' ? 'Exit Lock Screen' : 'Test Lock Screen Widget'}</span>
          </button>

          <a
            href="/aurum-music-android-project.zip"
            download="aurum-music-android-project.zip"
            className="flex items-center space-x-2 px-4 py-2 bg-gradient-to-r from-purple-600 via-purple-500 to-amber-500 hover:from-purple-500 hover:to-amber-400 text-white font-semibold text-xs rounded-xl shadow-lg shadow-purple-950 transition-all hover:scale-105 active:scale-95"
          >
            <Download className="w-3.5 h-3.5" />
            <span>Download Project ZIP</span>
          </a>

          <div className="flex bg-[#19112B] p-1 rounded-xl border border-[#2D1F4D]">
            <button
              onClick={() => setActiveTab('preview')}
              className={`flex items-center space-x-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold transition-all ${
                activeTab === 'preview' ? 'bg-purple-600 text-white shadow' : 'text-neutral-400 hover:text-white'
              }`}
            >
              <Smartphone className="w-3.5 h-3.5" />
              <span>Interactive Player</span>
            </button>
            <button
              onClick={() => setActiveTab('code')}
              className={`flex items-center space-x-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold transition-all ${
                activeTab === 'code' ? 'bg-purple-600 text-white shadow' : 'text-neutral-400 hover:text-white'
              }`}
            >
              <FileCode className="w-3.5 h-3.5" />
              <span>Native Code</span>
            </button>
            <button
              onClick={() => setActiveTab('build-guide')}
              className={`flex items-center space-x-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold transition-all ${
                activeTab === 'build-guide' ? 'bg-purple-600 text-white shadow' : 'text-neutral-400 hover:text-white'
              }`}
            >
              <Terminal className="w-3.5 h-3.5" />
              <span>Build APK Guide</span>
            </button>
          </div>
        </div>
      </header>

      {/* Main Container */}
      <main className="flex-1 max-w-7xl w-full mx-auto p-6 grid grid-cols-1 lg:grid-cols-12 gap-6">

        {/* Tab 1: Interactive Android Phone Simulator */}
        {activeTab === 'preview' && (
          <>
            {/* Phone Frame */}
            <div className="lg:col-span-5 flex justify-center">
              <div className="w-[380px] h-[770px] bg-[#0A0A0E] rounded-[44px] border-[6px] border-[#2B1B47] shadow-[0_0_60px_rgba(139,92,246,0.3)] flex flex-col overflow-hidden relative">

                {/* Top Status Bar */}
                <div className="h-7 bg-[#0A0A0E] flex items-center justify-between px-6 text-[10px] text-neutral-400 select-none z-20">
                  <span>10:52</span>
                  <div className="w-20 h-3.5 bg-black rounded-full border border-purple-950/60" />
                  <div className="flex items-center space-x-1.5">
                    <span className="text-[9px]">5G</span>
                    <span className="text-[9px]">100%</span>
                  </div>
                </div>

                {/* Phone App Bar (Only visible when unlocked) */}
                {simScreen !== 'lockScreen' && (
                  <div className="bg-[#100B1A] px-4 py-2 flex items-center justify-between border-b border-[#211538]">
                    <div className="flex items-center space-x-2.5">
                      <AurumMusicLogo size={28} />
                      <div>
                        <span className="font-bold text-sm text-white">Aurum Music</span>
                      </div>
                    </div>

                    <div className="flex items-center space-x-1">
                      <button
                        onClick={() => setSimScreen('search')}
                        className={`p-1.5 rounded-lg transition ${simScreen === 'search' ? 'text-amber-400 bg-purple-950' : 'text-neutral-300 hover:text-purple-400'}`}
                        title="Search tracks, artists, albums"
                      >
                        <Search className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => setSimScreen('equalizer')}
                        className={`p-1.5 rounded-lg transition ${simScreen === 'equalizer' ? 'text-purple-400 bg-purple-950' : 'text-neutral-300 hover:text-purple-400'}`}
                        title="Audio Equalizer"
                      >
                        <Sliders className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => setSimScreen('lockScreen')}
                        className="p-1.5 rounded-lg text-neutral-400 hover:text-amber-400 transition"
                        title="Simulate Lock Screen"
                      >
                        <Lock className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                )}

                {/* Toast Notification */}
                {repeatToast && (
                  <div className="absolute top-16 left-1/2 -translate-x-1/2 z-40 bg-purple-900/95 border border-amber-400/60 text-white text-xs font-semibold px-4 py-1.5 rounded-full shadow-2xl backdrop-blur-md animate-bounce">
                    {repeatToast}
                  </div>
                )}

                {/* Simulator Body */}
                <div className="flex-1 overflow-y-auto bg-gradient-to-b from-[#130B24] via-[#0D0817] to-[#0A0A0E] text-white relative">

                  {/* SCREEN: DYNAMIC LIVE CONTROL WIDGET FOR LOCK SCREEN */}
                  {simScreen === 'lockScreen' && (
                    <div className="h-full flex flex-col justify-between p-5 bg-gradient-to-b from-[#150D29] via-[#0C0719] to-[#050409] relative">
                      {/* Lock Screen Header with Clock */}
                      <div className="pt-4 text-center select-none">
                        <div className="flex items-center justify-center space-x-1.5 text-neutral-400 text-xs mb-1">
                          <Lock className="w-3.5 h-3.5 text-amber-400" />
                          <span>Device Locked</span>
                        </div>
                        <h1 className="text-5xl font-light tracking-tight text-white">10:52</h1>
                        <p className="text-xs text-purple-300 mt-1 font-medium">Thursday, October 24</p>
                      </div>

                      {/* DYNAMIC LIVE MEDIA CONTROL WIDGET */}
                      <div className="my-auto w-full p-4 rounded-3xl bg-[#1C1236]/90 backdrop-blur-xl border border-purple-500/40 shadow-[0_0_35px_rgba(139,92,246,0.25)] relative overflow-hidden space-y-3">
                        {/* Audio Output Route Indicator */}
                        <div className="flex items-center justify-between text-[11px] text-neutral-400 border-b border-[#2B1B4D] pb-2">
                          <div className="flex items-center space-x-1.5">
                            <Bluetooth className="w-3.5 h-3.5 text-purple-400" />
                            <span className="text-purple-300 font-semibold">Galaxy Buds2 Pro</span>
                          </div>
                          <div className="flex items-center space-x-1">
                            <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse" />
                            <span className="text-emerald-400 text-[10px] font-bold">LIVE PLAYBACK</span>
                          </div>
                        </div>

                        {/* Song Info Row */}
                        <div className="flex items-center space-x-3.5">
                          {/* Album Art with Ambient Glow */}
                          <div className={`w-14 h-14 rounded-2xl bg-gradient-to-br ${currentSong.coverColor} p-0.5 border border-amber-400/50 shadow-md flex-shrink-0 flex items-center justify-center relative`}>
                            <Disc className={`w-8 h-8 text-amber-300/80 ${isPlaying ? 'animate-spin' : ''}`} style={{ animationDuration: '6s' }} />
                          </div>

                          <div className="flex-1 min-w-0">
                            <h3 className="font-bold text-sm text-white truncate">{currentSong.title}</h3>
                            <p className="text-xs text-neutral-300 truncate">{currentSong.artist}</p>
                            <p className="text-[10px] text-purple-400 truncate">{currentSong.album} • {currentSong.genre}</p>
                          </div>

                          <button
                            onClick={() => toggleFavorite(currentSong.id)}
                            className="p-1.5 text-neutral-400 hover:text-rose-500 transition"
                          >
                            <Heart className={`w-5 h-5 ${currentSong.isFavorite ? 'text-rose-500 fill-current' : ''}`} />
                          </button>
                        </div>

                        {/* Dynamic Scrubber with Soundwaves */}
                        <div className="space-y-1 pt-1">
                          <div className="w-full bg-[#2A1B4B] h-1.5 rounded-full overflow-hidden cursor-pointer">
                            <div
                              className="bg-gradient-to-r from-purple-500 via-violet-400 to-amber-400 h-full rounded-full transition-all"
                              style={{ width: `${progressPercent}%` }}
                            />
                          </div>
                          <div className="flex justify-between text-[10px] text-neutral-400">
                            <span>{formatSeconds(playbackSec)}</span>
                            <span>-{formatSeconds(currentSong.durationSec - playbackSec)}</span>
                          </div>
                        </div>

                        {/* Live Control Buttons Row */}
                        <div className="flex items-center justify-between pt-1">
                          <button
                            onClick={() => setPlaybackSec(s => Math.max(0, s - 10))}
                            className="p-1.5 text-neutral-400 hover:text-white"
                            title="Rewind 10s"
                          >
                            <RotateCcw className="w-4 h-4" />
                          </button>

                          <button
                            onClick={() => setCurrentSongIndex(i => (i - 1 + songs.length) % songs.length)}
                            className="p-1.5 text-neutral-200 hover:text-white"
                            title="Previous track"
                          >
                            <SkipBack className="w-5 h-5 fill-current" />
                          </button>

                          {/* Big Live Play/Pause */}
                          <button
                            onClick={() => setIsPlaying(!isPlaying)}
                            className="w-11 h-11 rounded-full bg-gradient-to-tr from-purple-600 to-amber-400 p-[1.5px] shadow-lg hover:scale-105 active:scale-95 transition"
                          >
                            <div className="w-full h-full bg-[#180E2E] rounded-full flex items-center justify-center">
                              {isPlaying ? (
                                <Pause className="w-5 h-5 text-amber-300 fill-current" />
                              ) : (
                                <Play className="w-5 h-5 text-amber-300 fill-current ml-0.5" />
                              )}
                            </div>
                          </button>

                          <button
                            onClick={() => setCurrentSongIndex(i => (i + 1) % songs.length)}
                            className="p-1.5 text-neutral-200 hover:text-white"
                            title="Next track"
                          >
                            <SkipForward className="w-5 h-5 fill-current" />
                          </button>

                          <button
                            onClick={() => setPlaybackSec(s => Math.min(currentSong.durationSec, s + 10))}
                            className="p-1.5 text-neutral-400 hover:text-white"
                            title="Forward 10s"
                          >
                            <RotateCw className="w-4 h-4" />
                          </button>
                        </div>
                      </div>

                      {/* Unlock Swipe prompt */}
                      <div
                        onClick={() => setSimScreen('home')}
                        className="py-3 text-center text-xs text-neutral-400 cursor-pointer flex flex-col items-center hover:text-purple-300 transition"
                      >
                        <Unlock className="w-4 h-4 mb-1 text-purple-400 animate-bounce" />
                        <span>Tap to unlock phone & return to library</span>
                      </div>
                    </div>
                  )}

                  {/* SCREEN: HOME */}
                  {simScreen === 'home' && (
                    <div className="p-4 space-y-4">
                      {/* Search Bar */}
                      <div
                        onClick={() => setSimScreen('search')}
                        className="p-2.5 rounded-xl bg-[#160E2A] border border-[#2D1B4E] flex items-center space-x-2 text-xs text-neutral-400 cursor-pointer hover:border-purple-500/60 transition"
                      >
                        <Search className="w-3.5 h-3.5 text-purple-400" />
                        <span>Search in your local Hi-Fi library...</span>
                      </div>

                      {/* Hero Sanctuary Card with the Old Suggested Vector Logo */}
                      <div className="p-4 rounded-2xl bg-gradient-to-br from-[#29174A] via-[#1D0F35] to-[#120822] border border-[#3E236D] shadow-lg flex flex-col items-center text-center">
                        <AurumMusicLogo size={56} />

                        <span className="text-[10px] font-bold text-amber-400 tracking-widest mt-2">AURUM MUSIC</span>
                        <h2 className="text-base font-bold text-white">Your Sound Sanctuary</h2>
                        <p className="text-[11px] text-neutral-300">{songs.length} Tracks • {Object.keys(songsByGenre).length} Genres • {Object.keys(songsByArtist).length} Artists</p>

                        <div className="flex space-x-2 w-full mt-3">
                          <button
                            onClick={() => {
                              setCurrentSongIndex(0);
                              setIsPlaying(true);
                              setSimScreen('nowPlaying');
                            }}
                            className="flex-1 py-1.5 bg-gradient-to-r from-purple-600 to-purple-500 hover:from-purple-500 hover:to-purple-400 rounded-lg text-xs font-semibold flex items-center justify-center space-x-1 shadow"
                          >
                            <Play className="w-3.5 h-3.5 fill-current" />
                            <span>Play All</span>
                          </button>
                          <button
                            onClick={() => {
                              setIsShuffle(true);
                              setCurrentSongIndex(Math.floor(Math.random() * songs.length));
                              setIsPlaying(true);
                              setSimScreen('nowPlaying');
                            }}
                            className="flex-1 py-1.5 bg-[#1F1436] hover:bg-[#2A1B4A] text-purple-300 border border-purple-800/50 rounded-lg text-xs font-semibold flex items-center justify-center space-x-1"
                          >
                            <Shuffle className="w-3.5 h-3.5" />
                            <span>Shuffle</span>
                          </button>
                        </div>
                      </div>

                      {/* Recently Played */}
                      <div>
                        <h3 className="text-xs font-bold text-neutral-300 uppercase tracking-wider mb-2">Recently Played</h3>
                        <div className="flex space-x-2.5 overflow-x-auto pb-1 no-scrollbar">
                          {songs.slice(0, 4).map((song, i) => (
                            <div
                              key={song.id}
                              onClick={() => {
                                setCurrentSongIndex(i);
                                setIsPlaying(true);
                                setSimScreen('nowPlaying');
                              }}
                              className="w-32 flex-shrink-0 p-2 rounded-xl bg-[#170F2B] border border-[#2D1B4D] hover:border-purple-600/60 cursor-pointer transition"
                            >
                              <div className={`w-full aspect-square rounded-lg bg-gradient-to-br ${song.coverColor} flex items-center justify-center shadow`}>
                                <Music className="w-6 h-6 text-purple-300/80" />
                              </div>
                              <p className="text-xs font-semibold mt-1.5 truncate">{song.title}</p>
                              <p className="text-[10px] text-neutral-400 truncate">{song.artist}</p>
                            </div>
                          ))}
                        </div>
                      </div>

                      {/* NEW: LIBRARY SECTION (Songs listed by Playlists, Genre and Artist) */}
                      <div className="space-y-3 pt-1">
                        <div className="flex items-center justify-between">
                          <h3 className="text-sm font-bold text-white flex items-center space-x-1.5">
                            <Layers className="w-4 h-4 text-amber-400" />
                            <span>Library</span>
                          </h3>
                          <span className="text-[10px] text-purple-400 font-semibold uppercase tracking-wider">Classified Hub</span>
                        </div>

                        {/* 3 Library Switcher Pills */}
                        <div className="flex p-1 rounded-xl bg-[#160E2A] border border-[#2B1B4D]">
                          <button
                            onClick={() => setLibrarySubTab('playlists')}
                            className={`flex-1 py-1 rounded-lg text-xs font-semibold transition ${
                              librarySubTab === 'playlists' ? 'bg-purple-600 text-white shadow' : 'text-neutral-400 hover:text-white'
                            }`}
                          >
                            Playlists ({samplePlaylists.length})
                          </button>
                          <button
                            onClick={() => setLibrarySubTab('genre')}
                            className={`flex-1 py-1 rounded-lg text-xs font-semibold transition ${
                              librarySubTab === 'genre' ? 'bg-purple-600 text-white shadow' : 'text-neutral-400 hover:text-white'
                            }`}
                          >
                            Genre ({Object.keys(songsByGenre).length})
                          </button>
                          <button
                            onClick={() => setLibrarySubTab('artist')}
                            className={`flex-1 py-1 rounded-lg text-xs font-semibold transition ${
                              librarySubTab === 'artist' ? 'bg-purple-600 text-white shadow' : 'text-neutral-400 hover:text-white'
                            }`}
                          >
                            Artist ({Object.keys(songsByArtist).length})
                          </button>
                        </div>

                        {/* SUB-VIEW 1: PLAYLISTS */}
                        {librarySubTab === 'playlists' && (
                          <div className="space-y-2">
                            {samplePlaylists.map(pl => (
                              <div
                                key={pl.id}
                                onClick={() => {
                                  if (pl.songs.length > 0) {
                                    const idx = songs.findIndex(s => s.id === pl.songs[0].id);
                                    if (idx >= 0) setCurrentSongIndex(idx);
                                    setIsPlaying(true);
                                    setSimScreen('nowPlaying');
                                  }
                                }}
                                className="p-2.5 rounded-xl bg-[#160E28] hover:bg-[#201438] border border-[#2B1A45] flex items-center justify-between cursor-pointer transition"
                              >
                                <div className="flex items-center space-x-3">
                                  <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-purple-700 to-indigo-900 flex items-center justify-center text-amber-400">
                                    <Bookmark className="w-5 h-5 fill-current" />
                                  </div>
                                  <div>
                                    <p className="text-xs font-bold text-white">{pl.name}</p>
                                    <p className="text-[10px] text-neutral-400">{pl.desc}</p>
                                  </div>
                                </div>

                                <button className="px-2.5 py-1 bg-purple-600 hover:bg-purple-500 rounded-lg text-[10px] font-bold text-white flex items-center space-x-1">
                                  <Play className="w-3 h-3 fill-current" />
                                  <span>Play</span>
                                </button>
                              </div>
                            ))}
                          </div>
                        )}

                        {/* SUB-VIEW 2: GENRE */}
                        {librarySubTab === 'genre' && (
                          <div className="space-y-2.5">
                            {Object.entries(songsByGenre).map(([genreName, genreTracks]) => (
                              <div
                                key={genreName}
                                className="p-2.5 rounded-xl bg-[#160E28] border border-[#2B1A45] space-y-2"
                              >
                                <div className="flex items-center justify-between pb-1.5 border-b border-[#25173E]">
                                  <div className="flex items-center space-x-2">
                                    <div className="w-7 h-7 rounded-lg bg-amber-400/20 text-amber-400 flex items-center justify-center">
                                      <Flame className="w-4 h-4 fill-current" />
                                    </div>
                                    <div>
                                      <p className="text-xs font-bold text-white">{genreName}</p>
                                      <p className="text-[10px] text-purple-300">{genreTracks.length} tracks</p>
                                    </div>
                                  </div>

                                  <button
                                    onClick={() => {
                                      const firstSong = genreTracks[0];
                                      const idx = songs.findIndex(s => s.id === firstSong.id);
                                      if (idx >= 0) setCurrentSongIndex(idx);
                                      setIsPlaying(true);
                                      setSimScreen('nowPlaying');
                                    }}
                                    className="px-2.5 py-1 bg-purple-600 hover:bg-purple-500 rounded-md text-[10px] font-bold text-white flex items-center space-x-1"
                                  >
                                    <Play className="w-3 h-3 fill-current" />
                                    <span>Play Genre</span>
                                  </button>
                                </div>

                                {/* Songs in this genre */}
                                <div className="space-y-1">
                                  {genreTracks.map(song => (
                                    <div
                                      key={song.id}
                                      onClick={() => {
                                        const idx = songs.findIndex(s => s.id === song.id);
                                        setCurrentSongIndex(idx);
                                        setIsPlaying(true);
                                        setSimScreen('nowPlaying');
                                      }}
                                      className="p-1.5 rounded-lg bg-[#110A20] hover:bg-[#1E1236] flex items-center space-x-2 text-xs cursor-pointer"
                                    >
                                      <Music className="w-3.5 h-3.5 text-purple-400 flex-shrink-0" />
                                      <div className="flex-1 min-w-0">
                                        <p className="text-white font-medium truncate">{song.title}</p>
                                        <p className="text-[10px] text-neutral-400 truncate">{song.artist}</p>
                                      </div>
                                      <span className="text-[10px] text-neutral-500">{song.duration}</span>
                                    </div>
                                  ))}
                                </div>
                              </div>
                            ))}
                          </div>
                        )}

                        {/* SUB-VIEW 3: ARTIST */}
                        {librarySubTab === 'artist' && (
                          <div className="space-y-2.5">
                            {Object.entries(songsByArtist).map(([artistName, artistTracks]) => (
                              <div
                                key={artistName}
                                className="p-2.5 rounded-xl bg-[#160E28] border border-[#2B1A45] space-y-2"
                              >
                                <div className="flex items-center justify-between pb-1.5 border-b border-[#25173E]">
                                  <div className="flex items-center space-x-2">
                                    <div className="w-7 h-7 rounded-full bg-purple-900 flex items-center justify-center text-amber-400">
                                      <User className="w-3.5 h-3.5" />
                                    </div>
                                    <div>
                                      <p className="text-xs font-bold text-white">{artistName}</p>
                                      <p className="text-[10px] text-neutral-400">{artistTracks.length} {artistTracks.length === 1 ? 'song' : 'songs'}</p>
                                    </div>
                                  </div>

                                  <button
                                    onClick={() => {
                                      const firstSong = artistTracks[0];
                                      const idx = songs.findIndex(s => s.id === firstSong.id);
                                      if (idx >= 0) setCurrentSongIndex(idx);
                                      setIsPlaying(true);
                                      setSimScreen('nowPlaying');
                                    }}
                                    className="px-2.5 py-1 bg-purple-600 hover:bg-purple-500 rounded-md text-[10px] font-bold text-white flex items-center space-x-1"
                                  >
                                    <Play className="w-3 h-3 fill-current" />
                                    <span>Play Artist</span>
                                  </button>
                                </div>

                                {/* Songs by this Artist */}
                                <div className="space-y-1">
                                  {artistTracks.map(song => (
                                    <div
                                      key={song.id}
                                      onClick={() => {
                                        const idx = songs.findIndex(s => s.id === song.id);
                                        setCurrentSongIndex(idx);
                                        setIsPlaying(true);
                                        setSimScreen('nowPlaying');
                                      }}
                                      className="p-1.5 rounded-lg bg-[#110A20] hover:bg-[#1E1236] flex items-center space-x-2 text-xs cursor-pointer"
                                    >
                                      <Music className="w-3.5 h-3.5 text-purple-400 flex-shrink-0" />
                                      <div className="flex-1 min-w-0">
                                        <p className="text-white font-medium truncate">{song.title}</p>
                                        <p className="text-[10px] text-neutral-400 truncate">{song.album}</p>
                                      </div>
                                      <span className="text-[10px] text-neutral-500">{song.duration}</span>
                                    </div>
                                  ))}
                                </div>
                              </div>
                            ))}
                          </div>
                        )}
                      </div>
                    </div>
                  )}

                  {/* SCREEN: LIBRARY (Dedicated Screen from Bottom Menu) */}
                  {simScreen === 'library' && (
                    <div className="p-4 space-y-4">
                      {/* Library Header */}
                      <div className="flex items-center justify-between pb-1 border-b border-[#211538]">
                        <div className="flex items-center space-x-2.5">
                          <div className="w-9 h-9 rounded-xl bg-purple-900/60 border border-purple-600/40 flex items-center justify-center text-amber-400">
                            <Library className="w-5 h-5" />
                          </div>
                          <div>
                            <h2 className="text-base font-bold text-white">Your Library</h2>
                            <p className="text-[11px] text-neutral-400">Classified by Playlists, Genre & Artist</p>
                          </div>
                        </div>

                        <span className="text-xs px-2.5 py-1 rounded-full bg-purple-950 text-amber-400 border border-purple-800 font-bold">
                          {songs.length} Tracks
                        </span>
                      </div>

                      {/* 3 Classification Tabs */}
                      <div className="flex p-1 rounded-xl bg-[#160E2A] border border-[#2B1B4D]">
                        <button
                          onClick={() => setLibrarySubTab('playlists')}
                          className={`flex-1 py-1.5 rounded-lg text-xs font-semibold transition ${
                            librarySubTab === 'playlists' ? 'bg-purple-600 text-white shadow' : 'text-neutral-400 hover:text-white'
                          }`}
                        >
                          Playlists ({samplePlaylists.length})
                        </button>
                        <button
                          onClick={() => setLibrarySubTab('genre')}
                          className={`flex-1 py-1.5 rounded-lg text-xs font-semibold transition ${
                            librarySubTab === 'genre' ? 'bg-purple-600 text-white shadow' : 'text-neutral-400 hover:text-white'
                          }`}
                        >
                          Genre ({Object.keys(songsByGenre).length})
                        </button>
                        <button
                          onClick={() => setLibrarySubTab('artist')}
                          className={`flex-1 py-1.5 rounded-lg text-xs font-semibold transition ${
                            librarySubTab === 'artist' ? 'bg-purple-600 text-white shadow' : 'text-neutral-400 hover:text-white'
                          }`}
                        >
                          Artist ({Object.keys(songsByArtist).length})
                        </button>
                      </div>

                      {/* PLAYLISTS VIEW */}
                      {librarySubTab === 'playlists' && (
                        <div className="space-y-2.5">
                          {samplePlaylists.map(pl => (
                            <div
                              key={pl.id}
                              onClick={() => {
                                if (pl.songs.length > 0) {
                                  const idx = songs.findIndex(s => s.id === pl.songs[0].id);
                                  if (idx >= 0) setCurrentSongIndex(idx);
                                  setIsPlaying(true);
                                  setSimScreen('nowPlaying');
                                }
                              }}
                              className="p-3 rounded-2xl bg-[#160E28] hover:bg-[#201438] border border-[#2B1A45] flex items-center justify-between cursor-pointer transition shadow-sm"
                            >
                              <div className="flex items-center space-x-3">
                                <div className="w-11 h-11 rounded-xl bg-gradient-to-tr from-purple-700 via-violet-800 to-indigo-900 flex items-center justify-center text-amber-400 shadow">
                                  <Bookmark className="w-5 h-5 fill-current" />
                                </div>
                                <div>
                                  <p className="text-xs font-bold text-white">{pl.name}</p>
                                  <p className="text-[10px] text-neutral-400">{pl.desc}</p>
                                </div>
                              </div>

                              <button className="px-3 py-1 bg-gradient-to-r from-purple-600 to-purple-500 hover:from-purple-500 hover:to-purple-400 rounded-lg text-[10px] font-bold text-white flex items-center space-x-1 shadow">
                                <Play className="w-3 h-3 fill-current" />
                                <span>Play</span>
                              </button>
                            </div>
                          ))}
                        </div>
                      )}

                      {/* GENRE VIEW */}
                      {librarySubTab === 'genre' && (
                        <div className="space-y-3">
                          {Object.entries(songsByGenre).map(([genreName, genreTracks]) => (
                            <div
                              key={genreName}
                              className="p-3 rounded-2xl bg-[#160E28] border border-[#2B1A45] space-y-2.5 shadow-sm"
                            >
                              <div className="flex items-center justify-between pb-2 border-b border-[#25173E]">
                                <div className="flex items-center space-x-2">
                                  <div className="w-8 h-8 rounded-lg bg-amber-400/20 text-amber-400 flex items-center justify-center">
                                    <Flame className="w-4 h-4 fill-current" />
                                  </div>
                                  <div>
                                    <p className="text-xs font-bold text-white">{genreName}</p>
                                    <p className="text-[10px] text-purple-300">{genreTracks.length} tracks</p>
                                  </div>
                                </div>

                                <button
                                  onClick={() => {
                                    const firstSong = genreTracks[0];
                                    const idx = songs.findIndex(s => s.id === firstSong.id);
                                    if (idx >= 0) setCurrentSongIndex(idx);
                                    setIsPlaying(true);
                                    setSimScreen('nowPlaying');
                                  }}
                                  className="px-2.5 py-1 bg-purple-600 hover:bg-purple-500 rounded-lg text-[10px] font-bold text-white flex items-center space-x-1"
                                >
                                  <Play className="w-3 h-3 fill-current" />
                                  <span>Play Genre</span>
                                </button>
                              </div>

                              {/* Songs in this genre */}
                              <div className="space-y-1">
                                {genreTracks.map(song => (
                                  <div
                                    key={song.id}
                                    onClick={() => {
                                      const idx = songs.findIndex(s => s.id === song.id);
                                      setCurrentSongIndex(idx);
                                      setIsPlaying(true);
                                      setSimScreen('nowPlaying');
                                    }}
                                    className="p-1.5 rounded-lg bg-[#110A20] hover:bg-[#1E1236] flex items-center space-x-2 text-xs cursor-pointer"
                                  >
                                    <Music className="w-3.5 h-3.5 text-purple-400 flex-shrink-0" />
                                    <div className="flex-1 min-w-0">
                                      <p className="text-white font-medium truncate">{song.title}</p>
                                      <p className="text-[10px] text-neutral-400 truncate">{song.artist}</p>
                                    </div>
                                    <span className="text-[10px] text-neutral-500">{song.duration}</span>
                                  </div>
                                ))}
                              </div>
                            </div>
                          ))}
                        </div>
                      )}

                      {/* ARTIST VIEW */}
                      {librarySubTab === 'artist' && (
                        <div className="space-y-3">
                          {Object.entries(songsByArtist).map(([artistName, artistTracks]) => (
                            <div
                              key={artistName}
                              className="p-3 rounded-2xl bg-[#160E28] border border-[#2B1A45] space-y-2.5 shadow-sm"
                            >
                              <div className="flex items-center justify-between pb-2 border-b border-[#25173E]">
                                <div className="flex items-center space-x-2">
                                  <div className="w-8 h-8 rounded-full bg-purple-900 flex items-center justify-center text-amber-400 border border-purple-700/50">
                                    <User className="w-4 h-4" />
                                  </div>
                                  <div>
                                    <p className="text-xs font-bold text-white">{artistName}</p>
                                    <p className="text-[10px] text-neutral-400">{artistTracks.length} {artistTracks.length === 1 ? 'song' : 'songs'}</p>
                                  </div>
                                </div>

                                <button
                                  onClick={() => {
                                    const firstSong = artistTracks[0];
                                    const idx = songs.findIndex(s => s.id === firstSong.id);
                                    if (idx >= 0) setCurrentSongIndex(idx);
                                    setIsPlaying(true);
                                    setSimScreen('nowPlaying');
                                  }}
                                  className="px-2.5 py-1 bg-purple-600 hover:bg-purple-500 rounded-lg text-[10px] font-bold text-white flex items-center space-x-1"
                                >
                                  <Play className="w-3 h-3 fill-current" />
                                  <span>Play Artist</span>
                                </button>
                              </div>

                              {/* Songs by this Artist */}
                              <div className="space-y-1">
                                {artistTracks.map(song => (
                                  <div
                                    key={song.id}
                                    onClick={() => {
                                      const idx = songs.findIndex(s => s.id === song.id);
                                      setCurrentSongIndex(idx);
                                      setIsPlaying(true);
                                      setSimScreen('nowPlaying');
                                    }}
                                    className="p-1.5 rounded-lg bg-[#110A20] hover:bg-[#1E1236] flex items-center space-x-2 text-xs cursor-pointer"
                                  >
                                    <Music className="w-3.5 h-3.5 text-purple-400 flex-shrink-0" />
                                    <div className="flex-1 min-w-0">
                                      <p className="text-white font-medium truncate">{song.title}</p>
                                      <p className="text-[10px] text-neutral-400 truncate">{song.album}</p>
                                    </div>
                                    <span className="text-[10px] text-neutral-500">{song.duration}</span>
                                  </div>
                                ))}
                              </div>
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  )}

                  {/* SCREEN: SONGS */}
                  {simScreen === 'songs' && (
                    <div className="p-4 space-y-3">
                      <div className="relative">
                        <Search className="w-3.5 h-3.5 absolute left-3 top-2.5 text-neutral-400" />
                        <input
                          type="text"
                          value={searchQuery}
                          onChange={(e) => setSearchQuery(e.target.value)}
                          placeholder="Filter songs, artists, genres..."
                          className="w-full pl-9 pr-3 py-1.5 rounded-xl bg-[#170F2B] border border-[#2E1C50] text-xs focus:outline-none focus:border-purple-500 text-white placeholder-neutral-500"
                        />
                      </div>

                      <div className="space-y-1.5">
                        {songs.filter(s => s.title.toLowerCase().includes(searchQuery.toLowerCase()) || s.artist.toLowerCase().includes(searchQuery.toLowerCase())).map((song, i) => {
                          const isThisPlaying = currentSong.id === song.id;
                          return (
                            <div
                              key={song.id}
                              onClick={() => {
                                setCurrentSongIndex(i);
                                setIsPlaying(true);
                                setSimScreen('nowPlaying');
                              }}
                              className={`p-2 rounded-xl flex items-center space-x-3 cursor-pointer transition ${
                                isThisPlaying ? 'bg-[#251542] border border-purple-500/50' : 'bg-[#150E26] hover:bg-[#1E1336] border border-[#261642]'
                              }`}
                            >
                              <div className={`w-9 h-9 rounded-lg bg-gradient-to-br ${song.coverColor} flex items-center justify-center flex-shrink-0`}>
                                <Music className={`w-4 h-4 ${isThisPlaying ? 'text-amber-400 animate-pulse' : 'text-purple-300'}`} />
                              </div>
                              <div className="flex-1 min-w-0">
                                <p className={`text-xs font-semibold truncate ${isThisPlaying ? 'text-purple-300' : 'text-white'}`}>{song.title}</p>
                                <p className="text-[10px] text-neutral-400 truncate">{song.artist} • {song.genre}</p>
                              </div>
                              <button
                                onClick={(e) => {
                                  e.stopPropagation();
                                  toggleFavorite(song.id);
                                }}
                                className="p-1 text-neutral-400 hover:text-rose-500"
                              >
                                <Heart className={`w-3.5 h-3.5 ${song.isFavorite ? 'text-rose-500 fill-current' : ''}`} />
                              </button>
                            </div>
                          );
                        })}
                      </div>
                    </div>
                  )}

                  {/* SCREEN: SEARCH */}
                  {simScreen === 'search' && (
                    <div className="p-4 space-y-3">
                      <div className="flex items-center space-x-2">
                        <div className="relative flex-1">
                          <Search className="w-4 h-4 absolute left-3 top-2.5 text-neutral-400" />
                          <input
                            type="text"
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            placeholder="Search songs, artists, genres..."
                            autoFocus
                            className="w-full pl-9 pr-8 py-2 rounded-xl bg-[#170F2B] border border-purple-500/50 text-xs focus:outline-none focus:border-amber-400 text-white placeholder-neutral-500"
                          />
                          {searchQuery && (
                            <button
                              onClick={() => setSearchQuery('')}
                              className="absolute right-2.5 top-2.5 text-neutral-400 hover:text-white"
                            >
                              <X className="w-3.5 h-3.5" />
                            </button>
                          )}
                        </div>
                        <button
                          onClick={() => setSimScreen('home')}
                          className="text-xs font-semibold text-purple-300 hover:text-white"
                        >
                          Cancel
                        </button>
                      </div>

                      <div className="space-y-1.5 mt-2">
                        {songs
                          .filter(s =>
                            s.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                            s.artist.toLowerCase().includes(searchQuery.toLowerCase()) ||
                            s.genre.toLowerCase().includes(searchQuery.toLowerCase())
                          )
                          .map((song, i) => (
                            <div
                              key={song.id}
                              onClick={() => {
                                const realIdx = songs.findIndex(s => s.id === song.id);
                                setCurrentSongIndex(realIdx);
                                setIsPlaying(true);
                                setSimScreen('nowPlaying');
                              }}
                              className="p-2 rounded-xl bg-[#150E26] hover:bg-[#1E1336] border border-[#261642] flex items-center space-x-3 cursor-pointer transition"
                            >
                              <div className={`w-9 h-9 rounded-lg bg-gradient-to-br ${song.coverColor} flex items-center justify-center flex-shrink-0`}>
                                <Music className="w-4 h-4 text-purple-300" />
                              </div>
                              <div className="flex-1 min-w-0">
                                <p className="text-xs font-semibold truncate text-white">{song.title}</p>
                                <p className="text-[10px] text-amber-300/80 truncate">{song.artist} • {song.genre}</p>
                              </div>
                              <span className="text-[10px] text-neutral-400">{song.duration}</span>
                            </div>
                          ))}
                      </div>
                    </div>
                  )}

                  {/* SCREEN: NOW PLAYING */}
                  {simScreen === 'nowPlaying' && (
                    <div className="p-6 flex flex-col items-center justify-between min-h-full">
                      <div className="text-center">
                        <span className="text-[10px] font-bold text-purple-400 uppercase tracking-wider">PLAYING FROM LIBRARY</span>
                        <p className="text-xs text-neutral-400 truncate max-w-[200px]">{currentSong.album}</p>
                      </div>

                      {/* Large Album Artwork with Old Suggested Vector Logo in Center */}
                      <div className="relative my-4">
                        <div className="w-56 h-56 rounded-3xl bg-gradient-to-br from-amber-500/25 via-purple-600/30 to-violet-900/40 blur-xl absolute -inset-2 opacity-80" />
                        <div className={`w-56 h-56 rounded-3xl bg-gradient-to-br ${currentSong.coverColor} border-2 border-amber-400/50 relative shadow-2xl flex flex-col items-center justify-center p-4 overflow-hidden`}>
                          <div className={`w-36 h-36 rounded-full border-2 border-amber-400/60 shadow-[0_0_25px_rgba(250,204,21,0.5)] overflow-hidden flex items-center justify-center bg-[#0E0A1A] ${isPlaying ? 'animate-spin' : ''}`} style={{ animationDuration: '10s' }}>
                            <AurumMusicLogo size={100} />
                          </div>
                          <div className="absolute bottom-2.5 text-center">
                            <span className="text-[10px] font-extrabold text-amber-300 tracking-widest drop-shadow">AURUM MASTER AUDIO</span>
                          </div>
                        </div>
                      </div>

                      {/* Title & Artist & Favorite */}
                      <div className="w-full flex items-center justify-between my-2">
                        <div className="min-w-0 pr-2">
                          <h2 className="text-base font-bold text-white truncate">{currentSong.title}</h2>
                          <p className="text-xs text-purple-300/90 truncate block text-left">
                            {currentSong.artist} • {currentSong.genre}
                          </p>
                        </div>
                        <button
                          onClick={() => toggleFavorite(currentSong.id)}
                          className="p-2 text-neutral-400 hover:text-rose-500 transition"
                        >
                          <Heart className={`w-5 h-5 ${currentSong.isFavorite ? 'text-rose-500 fill-current' : ''}`} />
                        </button>
                      </div>

                      {/* Progress Bar & Timing */}
                      <div className="w-full space-y-1">
                        <div className="w-full bg-[#201538] h-1.5 rounded-full overflow-hidden cursor-pointer">
                          <div
                            className="bg-gradient-to-r from-purple-500 to-amber-400 h-full rounded-full transition-all"
                            style={{ width: `${progressPercent}%` }}
                          />
                        </div>
                        <div className="flex justify-between text-[10px] text-neutral-400">
                          <span>{formatSeconds(playbackSec)}</span>
                          <span>-{formatSeconds(currentSong.durationSec - playbackSec)}</span>
                        </div>
                      </div>

                      {/* Main Playback Controls with REPEAT ONE support */}
                      <div className="flex items-center justify-between w-full mt-3">
                        <button
                          onClick={() => {
                            setIsShuffle(!isShuffle);
                            showToast(!isShuffle ? 'Shuffle On' : 'Shuffle Off');
                          }}
                          className={`p-2 transition ${isShuffle ? 'text-amber-400' : 'text-neutral-400'}`}
                          title="Shuffle"
                        >
                          <Shuffle className="w-4 h-4" />
                        </button>

                        <button
                          onClick={() => setCurrentSongIndex(i => (i - 1 + songs.length) % songs.length)}
                          className="p-2 text-white hover:text-purple-300"
                        >
                          <SkipBack className="w-5 h-5 fill-current" />
                        </button>

                        <button
                          onClick={() => setPlaybackSec(s => Math.max(0, s - 10))}
                          className="p-1.5 text-neutral-400 hover:text-white text-xs"
                          title="Rewind 10s"
                        >
                          <RotateCcw className="w-4 h-4" />
                        </button>

                        {/* Big Play/Pause Button */}
                        <button
                          onClick={() => setIsPlaying(!isPlaying)}
                          className="w-13 h-13 rounded-full bg-gradient-to-tr from-purple-600 to-purple-500 hover:from-purple-500 hover:to-purple-400 p-[1.5px] shadow-lg shadow-purple-900/50 hover:scale-105 active:scale-95 transition"
                        >
                          <div className="w-full h-full bg-[#180E2E] rounded-full flex items-center justify-center">
                            {isPlaying ? (
                              <Pause className="w-6 h-6 text-amber-400 fill-current" />
                            ) : (
                              <Play className="w-6 h-6 text-amber-400 fill-current ml-0.5" />
                            )}
                          </div>
                        </button>

                        <button
                          onClick={() => setPlaybackSec(s => Math.min(currentSong.durationSec, s + 10))}
                          className="p-1.5 text-neutral-400 hover:text-white text-xs"
                          title="Forward 10s"
                        >
                          <RotateCw className="w-4 h-4" />
                        </button>

                        <button
                          onClick={() => setCurrentSongIndex(i => (i + 1) % songs.length)}
                          className="p-2 text-white hover:text-purple-300"
                        >
                          <SkipForward className="w-5 h-5 fill-current" />
                        </button>

                        {/* Repeat Mode with distinct badge for Repeat One */}
                        <button
                          onClick={handleToggleRepeat}
                          className={`p-2 relative transition rounded-lg ${
                            repeatMode === 'one'
                              ? 'text-amber-400 bg-amber-400/20'
                              : repeatMode === 'all'
                              ? 'text-purple-300 bg-purple-900/40'
                              : 'text-neutral-400'
                          }`}
                          title={repeatMode === 'one' ? 'Repeat One Active' : repeatMode === 'all' ? 'Repeat All Active' : 'Repeat Off'}
                        >
                          <Repeat className="w-4 h-4" />
                          {repeatMode === 'one' && (
                            <span className="absolute -top-1 -right-1 text-[9px] font-black bg-amber-400 text-black px-1 rounded-full leading-none">
                              1
                            </span>
                          )}
                        </button>
                      </div>

                      {/* Equalizer & Lock Screen Widget Launcher */}
                      <div className="w-full flex space-x-2 mt-4 pt-3 border-t border-[#23153D]">
                        <button
                          onClick={() => setSimScreen('equalizer')}
                          className="flex-1 py-1.5 rounded-lg bg-[#180F2C] hover:bg-[#23163E] border border-purple-900/40 text-[11px] font-semibold text-purple-300 flex items-center justify-center space-x-1"
                        >
                          <Sliders className="w-3.5 h-3.5 text-amber-400" />
                          <span>Equalizer</span>
                        </button>
                        <button
                          onClick={() => setSimScreen('lockScreen')}
                          className="flex-1 py-1.5 rounded-lg bg-[#180F2C] hover:bg-[#23163E] border border-amber-400/40 text-[11px] font-semibold text-amber-300 flex items-center justify-center space-x-1"
                        >
                          <Lock className="w-3.5 h-3.5 text-amber-400" />
                          <span>Lock Screen Widget</span>
                        </button>
                      </div>
                    </div>
                  )}

                  {/* SCREEN: EQUALIZER */}
                  {simScreen === 'equalizer' && (
                    <div className="p-4 space-y-4">
                      <div className="flex items-center justify-between">
                        <div>
                          <h3 className="text-sm font-bold">Hardware Audio FX</h3>
                          <p className="text-[10px] text-neutral-400">Android AudioEffect DSP Engine</p>
                        </div>
                        <label className="relative inline-flex items-center cursor-pointer">
                          <input
                            type="checkbox"
                            checked={eqEnabled}
                            onChange={(e) => setEqEnabled(e.target.checked)}
                            className="sr-only peer"
                          />
                          <div className="w-9 h-5 bg-[#25173E] peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-amber-400 after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-purple-600"></div>
                        </label>
                      </div>

                      <div>
                        <span className="text-[10px] font-bold text-neutral-400 uppercase tracking-wider">PRESET PROFILES</span>
                        <div className="flex space-x-1.5 overflow-x-auto text-[11px] mt-1.5 pb-1">
                          {PRESETS.map(preset => (
                            <button
                              key={preset}
                              onClick={() => setEqPreset(preset)}
                              className={`px-3 py-1 rounded-full whitespace-nowrap transition ${
                                eqPreset === preset ? 'bg-purple-600 font-bold text-white' : 'bg-[#180F2C] text-neutral-300 hover:bg-[#24163E]'
                              }`}
                            >
                              {preset}
                            </button>
                          ))}
                        </div>
                      </div>

                      <div className="p-3.5 rounded-2xl bg-[#140D26] border border-[#2B1948] space-y-3">
                        <span className="text-[11px] font-semibold text-purple-300">5-Band Parametric Equalizer</span>
                        {['60 Hz (Sub Bass)', '230 Hz (Bass)', '910 Hz (Mids)', '3.6 kHz (Highs)', '14 kHz (Air)'].map((bandName, idx) => (
                          <div key={bandName} className="space-y-1">
                            <div className="flex justify-between text-[10px]">
                              <span className="text-neutral-300">{bandName}</span>
                              <span className="text-amber-400 font-mono">+{bands[idx] || 0} dB</span>
                            </div>
                            <input
                              type="range"
                              min="-12"
                              max="12"
                              value={bands[idx] || 0}
                              onChange={(e) => {
                                const newBands = [...bands];
                                newBands[idx] = parseInt(e.target.value);
                                setBands(newBands);
                              }}
                              className="w-full accent-purple-500 h-1 bg-[#261545] rounded-lg cursor-pointer"
                            />
                          </div>
                        ))}
                      </div>

                      <div className="grid grid-cols-2 gap-3">
                        <div className="p-3 rounded-xl bg-[#140D26] border border-[#2B1948] text-center">
                          <p className="text-[11px] font-semibold text-neutral-300">Bass Boost</p>
                          <p className="text-sm font-bold text-amber-400 mt-1">{bassStrength}%</p>
                          <input
                            type="range"
                            min="0"
                            max="100"
                            value={bassStrength}
                            onChange={(e) => setBassStrength(parseInt(e.target.value))}
                            className="w-full accent-amber-400 h-1 bg-[#261545] rounded-lg mt-2 cursor-pointer"
                          />
                        </div>

                        <div className="p-3 rounded-xl bg-[#140D26] border border-[#2B1948] text-center">
                          <p className="text-[11px] font-semibold text-neutral-300">3D Virtualizer</p>
                          <p className="text-sm font-bold text-purple-400 mt-1">{virtualizerStrength}%</p>
                          <input
                            type="range"
                            min="0"
                            max="100"
                            value={virtualizerStrength}
                            onChange={(e) => setVirtualizerStrength(parseInt(e.target.value))}
                            className="w-full accent-purple-500 h-1 bg-[#261545] rounded-lg mt-2 cursor-pointer"
                          />
                        </div>
                      </div>
                    </div>
                  )}

                </div>

                {/* Mini Player Bar (Visible on bottom of Home, Songs, Search when not locked) */}
                {simScreen !== 'nowPlaying' && simScreen !== 'lockScreen' && (
                  <div
                    onClick={() => setSimScreen('nowPlaying')}
                    className="h-13 bg-[#170E2B] border-t border-[#291745] px-3 flex items-center justify-between cursor-pointer hover:bg-[#1F1338] transition"
                  >
                    <div className="flex items-center space-x-2.5 min-w-0">
                      <div className={`w-8 h-8 rounded-lg bg-gradient-to-br ${currentSong.coverColor} flex items-center justify-center flex-shrink-0`}>
                        <Music className="w-4 h-4 text-purple-200" />
                      </div>
                      <div className="min-w-0">
                        <p className="text-xs font-semibold text-white truncate">{currentSong.title}</p>
                        <p className="text-[10px] text-neutral-400 truncate">{currentSong.artist}</p>
                      </div>
                    </div>

                    <div className="flex items-center space-x-1" onClick={(e) => e.stopPropagation()}>
                      <button
                        onClick={() => setIsPlaying(!isPlaying)}
                        className="p-1.5 text-amber-400 hover:scale-110 transition"
                      >
                        {isPlaying ? <Pause className="w-4 h-4 fill-current" /> : <Play className="w-4 h-4 fill-current" />}
                      </button>
                      <button
                        onClick={() => setCurrentSongIndex(i => (i + 1) % songs.length)}
                        className="p-1.5 text-neutral-300 hover:text-white"
                      >
                        <SkipForward className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                )}

                {/* Bottom Android Navigation Bar (Home, Songs, Lock Screen Widget, Search, Audio FX) */}
                {simScreen !== 'lockScreen' && (
                  <div className="h-14 bg-[#0D0818] border-t border-[#1F1235] grid grid-cols-5 text-[10px] text-neutral-400 select-none">
                    <button
                      onClick={() => setSimScreen('home')}
                      className={`flex flex-col items-center justify-center space-y-0.5 ${simScreen === 'home' ? 'text-amber-400 font-bold' : 'hover:text-white'}`}
                    >
                      <Music className="w-4 h-4" />
                      <span>Home</span>
                    </button>

                    <button
                      onClick={() => setSimScreen('songs')}
                      className={`flex flex-col items-center justify-center space-y-0.5 ${simScreen === 'songs' ? 'text-amber-400 font-bold' : 'hover:text-white'}`}
                    >
                      <ListMusic className="w-4 h-4" />
                      <span>Songs</span>
                    </button>

                    <button
                      onClick={() => setSimScreen('library')}
                      className={`flex flex-col items-center justify-center space-y-0.5 ${simScreen === 'library' ? 'text-amber-400 font-bold' : 'hover:text-white'}`}
                    >
                      <Library className="w-4 h-4" />
                      <span>Library</span>
                    </button>

                    <button
                      onClick={() => setSimScreen('search')}
                      className={`flex flex-col items-center justify-center space-y-0.5 ${simScreen === 'search' ? 'text-amber-400 font-bold' : 'hover:text-white'}`}
                    >
                      <Search className="w-4 h-4" />
                      <span>Search</span>
                    </button>

                    <button
                      onClick={() => setSimScreen('equalizer')}
                      className={`flex flex-col items-center justify-center space-y-0.5 ${simScreen === 'equalizer' ? 'text-amber-400 font-bold' : 'hover:text-white'}`}
                    >
                      <Sliders className="w-4 h-4" />
                      <span>Equalizer</span>
                    </button>
                  </div>
                )}
              </div>
            </div>

            {/* Right Information Panel */}
            <div className="lg:col-span-7 space-y-5">
              {/* Feature Checklist Box */}
              <div className="p-5 rounded-2xl bg-[#110B21] border border-[#2B1B47]">
                <div className="flex items-center justify-between mb-3">
                  <h2 className="text-base font-bold text-white flex items-center space-x-2">
                    <Sparkles className="w-4 h-4 text-amber-400" />
                    <span>Updated Features & Requirements</span>
                  </h2>
                  <span className="text-xs px-2.5 py-1 rounded-full bg-purple-950 text-purple-300 border border-purple-800 font-semibold">
                    Native Android Implementation
                  </span>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-2.5 text-xs text-neutral-300">
                  <div className="flex items-start space-x-2 p-2 rounded-xl bg-[#160E28] border border-purple-900/30">
                    <CheckCircle2 className="w-4 h-4 text-amber-400 flex-shrink-0 mt-0.5" />
                    <div>
                      <p className="font-semibold text-white">Classic Suggested Logo</p>
                      <p className="text-[11px] text-neutral-400">Restored the iconic violet headphones with gold note & equalizer waveform</p>
                    </div>
                  </div>

                  <div className="flex items-start space-x-2 p-2 rounded-xl bg-[#160E28] border border-purple-900/30">
                    <CheckCircle2 className="w-4 h-4 text-amber-400 flex-shrink-0 mt-0.5" />
                    <div>
                      <p className="font-semibold text-white">Home Library Hub</p>
                      <p className="text-[11px] text-neutral-400">Classified by Playlists, Genre, and Artist with instant playback</p>
                    </div>
                  </div>

                  <div className="flex items-start space-x-2 p-2 rounded-xl bg-[#160E28] border border-purple-900/30">
                    <CheckCircle2 className="w-4 h-4 text-amber-400 flex-shrink-0 mt-0.5" />
                    <div>
                      <p className="font-semibold text-white">Dynamic Lock Screen Live Widget</p>
                      <p className="text-[11px] text-neutral-400">Android MediaStyle live control widget active on lock screen when app is playing</p>
                    </div>
                  </div>

                  <div className="flex items-start space-x-2 p-2 rounded-xl bg-[#160E28] border border-purple-900/30">
                    <CheckCircle2 className="w-4 h-4 text-amber-400 flex-shrink-0 mt-0.5" />
                    <div>
                      <p className="font-semibold text-white">🔂 Repeat ONE Feature</p>
                      <p className="text-[11px] text-neutral-400">ExoPlayer hardware-level seamless track loop with badge "1"</p>
                    </div>
                  </div>
                </div>
              </div>

              {/* Lock Screen Live Widget Feature Spotlight */}
              <div className="p-5 rounded-2xl bg-gradient-to-r from-[#1D1136] to-[#120A24] border border-[#3E2469] flex items-center justify-between gap-4">
                <div>
                  <div className="flex items-center space-x-2">
                    <Lock className="w-4 h-4 text-amber-400" />
                    <h3 className="text-sm font-bold text-white">Lock Screen Live Control Widget Active</h3>
                  </div>
                  <p className="text-xs text-neutral-300 mt-1">
                    Powered by Android 13/14/15 MediaSessionService & MediaStyle notifications with public lock-screen visibility, interactive scrubber, and Bluetooth route controls.
                  </p>
                </div>
                <button
                  onClick={() => setSimScreen(s => s === 'lockScreen' ? 'home' : 'lockScreen')}
                  className="px-4 py-2 bg-gradient-to-r from-purple-600 to-amber-500 hover:from-purple-500 hover:to-amber-400 text-white font-bold text-xs rounded-xl shadow-lg flex items-center space-x-1.5 flex-shrink-0 transition hover:scale-105 active:scale-95"
                >
                  <Lock className="w-3.5 h-3.5" />
                  <span>{simScreen === 'lockScreen' ? 'Back to App' : 'Try Lock Screen'}</span>
                </button>
              </div>

              {/* Build Instructions */}
              <div className="p-4 rounded-xl bg-[#0D0917] border border-[#23153A] text-xs font-mono text-neutral-300 space-y-2">
                <div className="flex items-center justify-between text-neutral-400 text-[11px]">
                  <span>BUILD COMMANDS (WITHOUT ANDROID STUDIO)</span>
                  <span className="text-amber-400">app-debug.apk</span>
                </div>
                <div className="bg-[#06040A] p-2.5 rounded-lg border border-[#1A102C] text-purple-300">
                  <p className="text-neutral-500"># Windows PowerShell / CMD:</p>
                  <p className="text-amber-300">.\gradlew.bat assembleDebug</p>
                  <p className="text-neutral-500 mt-2"># Mac / Linux Terminal:</p>
                  <p className="text-amber-300">./gradlew assembleDebug</p>
                </div>
              </div>
            </div>
          </>
        )}

        {/* Tab 2: Native Android Source Code Explorer */}
        {activeTab === 'code' && (
          <div className="lg:col-span-12 grid grid-cols-1 lg:grid-cols-12 gap-6">
            <div className="lg:col-span-4 bg-[#110B21] rounded-2xl border border-[#2B1B47] p-4 h-[650px] overflow-y-auto space-y-1">
              <h3 className="text-xs font-bold text-neutral-400 uppercase tracking-wider px-2 py-1">Project Files</h3>

              {[
                { path: 'app/src/main/res/drawable/ic_aurum_logo.xml', label: 'ic_aurum_logo.xml', tag: 'Restored Logo' },
                { path: 'app/src/main/java/com/aurummusic/player/ui/screens/HomeScreen.kt', label: 'HomeScreen.kt', tag: 'Library Hub' },
                { path: 'app/src/main/java/com/aurummusic/player/playback/AurumMediaSessionService.kt', label: 'AurumMediaSessionService.kt', tag: 'Lock Widget' },
                { path: 'app/src/main/java/com/aurummusic/player/ui/screens/NowPlayingScreen.kt', label: 'NowPlayingScreen.kt', tag: 'Repeat One' },
                { path: 'app/src/main/java/com/aurummusic/player/data/repository/MusicRepository.kt', label: 'MusicRepository.kt', tag: 'Genres & Playlists' },
                { path: 'app/src/main/AndroidManifest.xml', label: 'AndroidManifest.xml', tag: 'Config' }
              ].map(f => (
                <button
                  key={f.path}
                  onClick={() => setSelectedFile(f.path)}
                  className={`w-full text-left px-3 py-2 rounded-xl text-xs flex items-center justify-between transition ${
                    selectedFile === f.path ? 'bg-purple-600 text-white font-semibold' : 'text-neutral-300 hover:bg-[#1C1236]'
                  }`}
                >
                  <span className="truncate pr-2">{f.label}</span>
                  <span className={`text-[10px] px-1.5 py-0.5 rounded ${selectedFile === f.path ? 'bg-purple-800 text-purple-200' : 'bg-[#1E1438] text-neutral-400'}`}>
                    {f.tag}
                  </span>
                </button>
              ))}
            </div>

            <div className="lg:col-span-8 bg-[#0B0714] rounded-2xl border border-[#2B1B47] p-5 h-[650px] flex flex-col">
              <div className="flex items-center justify-between pb-3 border-b border-[#211438] mb-3">
                <div className="flex items-center space-x-2">
                  <FileCode className="w-4 h-4 text-purple-400" />
                  <span className="font-mono text-xs text-purple-300">{selectedFile}</span>
                </div>
              </div>

              <div className="flex-1 overflow-auto bg-[#07040D] p-4 rounded-xl font-mono text-xs text-neutral-300 leading-relaxed">
                {selectedFile === 'app/src/main/res/drawable/ic_aurum_logo.xml' && (
                  <pre>{`<!-- Restored Iconic Aurum Music Vector Logo -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="128dp"
    android:height="128dp"
    android:viewportWidth="128"
    android:viewportHeight="128">

    <path android:fillColor="#0E0A1A" android:pathData="M64,64m-60,0a60,60 0,1 1,120 0a60,60 0,1 1,-120 0" />
    <path android:strokeColor="#8B5CF6" android:strokeWidth="3.5" android:pathData="M64,64m-52,0a52,52 0,1 1,104 0a52,52 0,1 1,-104 0" />

    <!-- Headphone Arch (Violet gradient) -->
    <path android:strokeColor="#C084FC" android:strokeWidth="5" android:strokeLineCap="round"
        android:pathData="M34,68 C34,42 46,30 64,30 C82,30 94,42 94,68" />

    <!-- Ear Cups -->
    <path android:fillColor="#8B5CF6" android:pathData="M28,64 L36,64 A4,4 0 0,1 40,68 L40,86 A4,4 0 0,1 36,90 L28,90 A4,4 0 0,1 24,86 L24,68 A4,4 0 0,1 28,64 Z" />
    <path android:fillColor="#8B5CF6" android:pathData="M92,64 L100,64 A4,4 0 0,1 104,68 L104,86 A4,4 0 0,1 100,90 L92,90 A4,4 0 0,1 88,86 L88,68 A4,4 0 0,1 92,64 Z" />

    <!-- Golden Music Note Centerpiece -->
    <path android:fillColor="#FACC15"
        android:pathData="M58,46 L78,41 L78,68 A8,8 0 1,1 70,60 A8,8 0 0,1 74,61 L74,48 L62,51 L62,74 A8,8 0 1,1 54,66 A8,8 0 0,1 58,67 L58,46 Z" />

    <!-- Equalizer Sound Wave Bars -->
    <path android:strokeColor="#FACC15" android:strokeWidth="3" android:strokeLineCap="round"
        android:pathData="M48,98 L48,104 M56,94 L56,108 M64,90 L64,112 M72,94 L72,108 M80,98 L80,104" />
</vector>`}</pre>
                )}

                {selectedFile === 'app/src/main/java/com/aurummusic/player/playback/AurumMediaSessionService.kt' && (
                  <pre>{`// Lock Screen Dynamic Live Control Widget Provider:
val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
    description = descriptionText
    setShowBadge(false)
    lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
}
// Android 13+ dynamically displays the interactive Live Media Player Widget on lock screen!`}</pre>
                )}

                {selectedFile !== 'app/src/main/res/drawable/ic_aurum_logo.xml' &&
                 selectedFile !== 'app/src/main/java/com/aurummusic/player/playback/AurumMediaSessionService.kt' && (
                  <pre>{`// Aurum Music Native Implementation
// File: ${selectedFile}
// Packaged inside public/aurum-music-android-project.zip.`}</pre>
                )}
              </div>
            </div>
          </div>
        )}

        {/* Tab 3: Build APK Guide */}
        {activeTab === 'build-guide' && (
          <div className="lg:col-span-12 space-y-6">
            <div className="p-6 rounded-2xl bg-[#110B21] border border-[#2B1B47]">
              <h2 className="text-lg font-bold text-white flex items-center space-x-2">
                <Terminal className="w-5 h-5 text-amber-400" />
                <span>Build app-debug.apk Without Android Studio</span>
              </h2>
              <p className="text-xs text-neutral-400 mt-1">
                Zero Android Studio installation needed. Use 1-Click GitHub Actions or your local command prompt.
              </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="p-6 rounded-2xl bg-[#140D26] border border-[#2E1C50] space-y-4">
                <div className="flex items-center space-x-2">
                  <div className="p-2 rounded-xl bg-purple-950 text-purple-300">
                    <Github className="w-5 h-5" />
                  </div>
                  <div>
                    <h3 className="font-bold text-sm text-white">Method 1: GitHub Cloud Build (Automated)</h3>
                    <p className="text-xs text-neutral-400">Builds in cloud without needing any SDK on your PC</p>
                  </div>
                </div>

                <ol className="text-xs text-neutral-300 space-y-2 list-decimal list-inside">
                  <li>Download project ZIP & push to your GitHub account.</li>
                  <li>Go to <span className="text-white font-semibold">Actions</span> tab &rarr; <span className="text-purple-300 font-semibold">Build Aurum Music APK</span>.</li>
                  <li>Click <span className="text-amber-400 font-semibold">Run workflow</span>.</li>
                  <li>Download <code className="text-amber-300 bg-black/40 px-1 py-0.5 rounded">app-debug.apk</code> from Artifacts in ~90 seconds!</li>
                </ol>
              </div>

              <div className="p-6 rounded-2xl bg-[#140D26] border border-[#2E1C50] space-y-4">
                <div className="flex items-center space-x-2">
                  <div className="p-2 rounded-xl bg-amber-950 text-amber-300">
                    <Terminal className="w-5 h-5" />
                  </div>
                  <div>
                    <h3 className="font-bold text-sm text-white">Method 2: Command Line (Windows / Mac)</h3>
                    <p className="text-xs text-neutral-400">Uses Gradle wrapper bundled in project</p>
                  </div>
                </div>

                <div className="bg-[#090512] p-3 rounded-xl border border-[#25173E] font-mono text-[11px] text-purple-300 space-y-1">
                  <p className="text-neutral-500"># Windows PowerShell / CMD:</p>
                  <p className="text-amber-300">.\gradlew.bat assembleDebug</p>
                  <p className="text-neutral-500 mt-2"># Mac / Linux Terminal:</p>
                  <p className="text-amber-300">./gradlew assembleDebug</p>
                </div>
              </div>
            </div>
          </div>
        )}

      </main>
    </div>
  );
}
