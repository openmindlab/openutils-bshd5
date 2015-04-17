insert into bar (id, s)
values (1, 'bar1');
insert into bar (id, s)
values (2, 'bar2');

insert into foo (id, bar_id, s)
values ( 1, 1, 'foo1_1' );
insert into foo (id, bar_id, s)
values ( 2, 1, 'foo1_2' );
insert into foo (id, bar_id, s)
values ( 3, 1, 'fooX_X' );
insert into foo (id, bar_id, s)
values ( 4, 2, 'foo2_1' );
insert into foo (id, bar_id, s)
values ( 5, 2, 'foo2_2' );
insert into foo (id, bar_id, s)
values ( 6, 2, 'fooX_X' );