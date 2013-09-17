#!/usr/bin/perl
# requires osm file on stdin, produces many csv files
use strict;

my %stations;
my @lines;
my %exits;
my @graph;

my $id, my $lon, my $lat;
my %tags;
my @nodes;
while(<>) {
    if( /<node.+id='([^']+)'.+lat='([^']+)'.+lon='([^']+)'/ ) {
        $id = $1;
        $lat = $2;
        $lon = $3;
        $lat =~ s/(\.\d{6})\d+/\1/;
        $lon =~ s/(\.\d{6})\d+/\1/;
    } elsif( /<way/ ) {
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
            $station->{name} = $tags{name};
            $station->{ref} = $tags{ref};
            $stations{$id} = $station;
        } elsif( $1 eq 'node' && $tags{railway} eq 'subway_entrance' ) {
            # subway entrance
            my $entrance = {};
            $entrance->{lat} = $lat;
            $entrance->{lon} = $lon;
            $entrance->{ref} = $tags{ref};
            $exits{$id} = $entrance;
        } elsif( $1 eq 'way' && $tags{railway} eq 'subway' ) {
            # subway line
            my $line = {};
            $line->{colour} = $tags{colour};
            $line->{ref} = $tags{ref};
            $line->{name} = $tags{name};
            push @lines, $line;
            # now attach stations to lines
            my $last_st;
            foreach (@nodes) {
                if( exists $stations{$_} ) {
                    $stations{$_}->{line} = $line->{ref};
                    if( $last_st ) {
                        my $gr = {};
                        $gr->{from} = $last_st;
                        $gr->{to} = $stations{$_};
                        $gr->{weight} = 3;
                        push @graph, $gr;
                    }
                    $last_st = $stations{$_};
                }
            }
        } elsif( $1 eq 'way' && $#nodes == 1 ) {
            my $is0st = exists $stations{$nodes[0]} ? 1 : exists $exits{$nodes[0]} ? 0 : -10;
            my $is1st = exists $stations{$nodes[1]} ? 1 : exists $exits{$nodes[1]} ? 0 : -10;
            if( $is0st + $is1st == 2 ) {
                my $gr = {};
                $gr->{from} = $stations{$nodes[0]};
                $gr->{to} = $stations{$nodes[1]};
                $gr->{weight} = 5;
                $gr->{inter} = 1;
                push @graph, $gr;
            } elsif( $is0st + $is1st == 1 ) {
                my $enp = $is0st ? 1 : 0;
                my $entrance = $exits{$nodes[$enp]};
                $entrance->{dir} = $tags{oneway} eq 'yes' || $tags{oneway} eq '1' ? $is0st ? 'out' : 'in' : 'both';
                $entrance->{station} = $stations{$nodes[1-$enp]};
            }
        }
        $lat = $lon = 0;
        undef @nodes;
        undef %tags;
    }
}

# Now read source csv and preserve information they contained

if( open GRAPH, '<graph.csv' ) {
    my %weights;
    while(<GRAPH>) {
        chomp;
        my @fields = split /;/;
        $weights{$fields[0].'_'.$fields[1]} = $fields[4] if $#fields >= 4 && $fields[4];
    }
    close GRAPH;
    $_->{weight} = $weights{$_->{from}->{ref}.'_'.$_->{to}->{ref}} foreach grep { exists $weights{$_->{from}->{ref}.'_'.$_->{to}->{ref}} } @graph;
}

my %tails;
if( open PORTALS, '<portals.csv' ) {
    while(<PORTALS>) {
        $tails{$1} = $2 if /^(\d+);(?:[^;]*;){5}(.+?)\s*$/;
    }
    close PORTALS;
}

my %itails;
if( open INTER, '<interchanges.csv' ) {
    while(<INTER>) {
        $itails{"$1_$2"} = $3 if /^(\d+);(\d+);(.+?)\s*$/;
    }
    close INTER;
}

# OK, time to write all csv back, overwriting old ones

open LINES, '>lines.csv' or die "Cannot open lines.csv: $!";
print LINES "id_line;name;color\n";
printf LINES "%d;%s;%s\n", $_->{ref}, $_->{name}, $_->{colour} foreach sort {$a->{ref} <=> $b->{ref}} @lines;
close LINES;

open STATIONS, '>stations.csv' or die "Cannot open stations.csv: $!";
print STATIONS "id_station;id_line;name;lat;lon\n";
printf STATIONS "%d;%d;%s;%s;%s\n", $_->{ref}, $_->{line}, $_->{name}, $_->{lat}, $_->{lon} foreach sort {$a->{ref} <=> $b->{ref}} values %stations;
close STATIONS;

open GRAPH, '>graph.csv' or die "Cannot open graph.csv: $!";
print GRAPH "id_from;id_to;name_from;name_to;cost\n";
printf GRAPH "%d;%d;%s;%s;%d\n", $_->{from}->{ref}, $_->{to}->{ref}, $_->{from}->{name}, $_->{to}->{name}, $_->{weight} foreach sort { $a->{from}->{ref} <=> $b->{from}->{ref} or $a->{to}->{ref} <=> $b->{to}->{ref} } @graph;
close GRAPH;

open PORTALS, '>portals.csv' or die "Cannot open portals.csv: $!";
print PORTALS "id_entrance;name;id_station;direction;lat;lon;max_width;min_step;min_step_ramp;lift;lift_minus_step;min_rail_width;max_rail_width;max_angle\n";
foreach(sort { $a->{station}->{ref} <=> $b->{station}->{ref} or $a->{ref} <=> $b->{ref} } grep { exists $_->{station} } values %exits) {
    my @res;
    my $exitid = $_->{station}->{ref} * 12 + $_->{ref} - 1 + 1000;
    push @res, $exitid;
    push @res, $_->{station}->{name}.'-'.$_->{ref};
    push @res, $_->{station}->{ref};
    push @res, $_->{dir};
    push @res, $_->{lat};
    push @res, $_->{lon};
    push @res, exists $tails{$exitid} ? $tails{$exitid} : ';;;;;;;';
    printf PORTALS "%s\n", join ';', @res;
}
close PORTALS;

open INTER, '>interchanges.csv' or die "Cannot open inter.csv: $!";
print INTER "station_from;station_to;max_width;min_step;min_step_ramp;lift;lift_minus_step;min_rail_width;max_rail_width;max_angle\n";
printf INTER "%d;%d;%s\n", $_->{from}->{ref}, $_->{to}->{ref}, $itails{$_->{from}->{ref}.'_'.$_->{to}->{ref}} || ';;;;;;;' foreach sort { $a->{from}->{ref} <=> $b->{from}->{ref} } grep { exists $_->{inter} } @graph;
close INTER;
