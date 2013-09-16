#!/usr/bin/perl
# Connect stations and nearby entrances with a highway=steps line
use strict;

my %stations;
my %exits;
my @graph;
my %existing;

my $id, my $lon, my $lat;
my %tags;
my @nodes;
my $wayid = -1;
while(<>) {
    if( /<node.+id='([^']+)'.+lat='([^']+)'.+lon='([^']+)'/ ) {
        $id = $1;
        $lat = $2;
        $lon = $3;
    } elsif( /<way.+id='([^']+)'/ ) {
        $wayid = $1-1 if $wayid >= $1;
    } elsif( /<tag k='([^']+)' v='([^']+)'/ ) {
        $tags{$1} = $2;
    } elsif( /<nd ref='([^']+)'/ ) {
        push @nodes, $1;
    } elsif( /<\/(node|way)/ ) {
        if( $1 eq 'node' && $tags{railway} eq 'station' ) {
            # subway station
            my $station = {};
            $station->{lat} = $lat;
            $station->{lon} = $lon;
            @stations{$id} = $station;
        } elsif( $1 eq 'node' && $tags{railway} eq 'subway_entrance' ) {
            # subway entrance
            my $entrance = {};
            $entrance->{lat} = $lat;
            $entrance->{lon} = $lon;
            $exits{$id} = $entrance;
        } elsif( $1 eq 'way' && $#nodes == 1 ) {
            $existing{$nodes[0]} = 1;
            $existing{$nodes[1]} = 1;
        }
        $lat = $lon = 0;
        undef @nodes;
        undef %tags;
    }
    
    if( /<\/osm/ ) {
        # remove exits that have ways connected to them
        delete @exits{keys %existing};

        # create connections between stations and exits
        foreach my $s (keys %stations) {
            push @graph, { station => $s, entrance => $_ } foreach grep { abs($stations{$s}->{lat} - $exits{$_}->{lat}) + abs($stations{$s}->{lon} - $exits{$_}->{lon}) < 0.005 } keys %exits;
        }

        # now write the graph back to the osm file
        foreach (@graph) {
            printf "  <way id='%d'>\n", $wayid--;
            printf "    <nd ref='%d' />\n", $_->{entrance};
            printf "    <nd ref='%d' />\n", $_->{station};
            print  "    <tag k='highway' v='steps' />\n";
            print  "  </way>\n";
        }
    }

    print;
}
