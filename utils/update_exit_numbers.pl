#!/usr/bin/perl
# Generate entrance refs where absent
use strict;

my %stations;
my %exits;

my $id;
my %tags;
my @nodes;

open METRO, '<spbmetro.osm' or die "Cannot open spbmetro.osm: $!";
while(<METRO>) {
    if( /<node.+id='([^']+)'/ ) {
        $id = $1;
    } elsif( /<tag k='([^']+)' v='([^']+)'/ ) {
        $tags{$1} = $2;
    } elsif( /<nd ref='([^']+)'/ ) {
        push @nodes, $1;
    } elsif( /<\/(node|way)/ ) {
        if( $1 eq 'node' && $tags{railway} eq 'station' ) {
            # subway station
            my $station = {};
            $station->{ref} = $tags{ref} if exists $tags{ref};
            $stations{$id} = $station;
        } elsif( $1 eq 'node' && $tags{railway} eq 'subway_entrance' ) {
            # subway entrance
            my $entrance = {};
            $entrance->{ref} = $tags{ref} if exists $tags{ref};
            $exits{$id} = $entrance;
        } elsif( $1 eq 'way' && $#nodes == 1 ) {
            my $is0st = exists $stations{$nodes[0]} ? 1 : exists $exits{$nodes[0]} ? 0 : -10;
            my $is1st = exists $stations{$nodes[1]} ? 1 : exists $exits{$nodes[1]} ? 0 : -10;
            if( $is0st + $is1st == 1 ) {
                my $enp = $is0st ? 1 : 0;
                my $entrance = $exits{$nodes[$enp]};
                $entrance->{dir} = $tags{oneway} eq 'yes' || $tags{oneway} eq '1' ? $is0st ? 'out' : 'in' : 'both';
                $entrance->{station} = $stations{$nodes[1-$enp]};
            }
        }
        undef @nodes;
        undef %tags;
    }
}

# Add ids to unnumbered exits

$_->{exits} = {} foreach values %stations;
$_->{station}->{exits}->{$_->{ref}} = 1 foreach grep { exists $_->{station} && $_->{ref} } values %exits;
foreach (grep { exists $_->{station} && !$_->{ref} } values %exits) {
    my $ref = $_->{dir} eq 'out' ? 2 : 1;
    $ref++ while exists $_->{station}->{exits}->{$ref};
    $_->{station}->{exits}->{$ref} = 1;
    $_->{ref} = $ref;
    $_->{newref} = 1;
}

# Write result

seek METRO, 0, 0;
while(<METRO>) {
    print;
    printf "    <tag k='ref' v='%d' />\n", $exits{$1}->{ref} if /<node id='([^']+)'/ && exists $exits{$1} && exists $exits{$1}->{newref};
}
close METRO;
